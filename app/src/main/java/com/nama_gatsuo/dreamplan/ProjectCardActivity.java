package com.nama_gatsuo.dreamplan;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.View.ProjectStatusView;
import com.nama_gatsuo.dreamplan.dao.ProjectDao;
import com.nama_gatsuo.dreamplan.model.Project;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

public class ProjectCardActivity extends ActionBarActivity {
    List<Project> projects;
    private SQLiteDatabase db;
    private ProjectDao projectDao;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_project_card);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        projectDao = new ProjectDao(db);

        projects = projectDao.findAll();

        projects.add(new Project());

        ListView lv = (ListView) findViewById(R.id.project_list);
        ProjectCardAdapter adapter = new ProjectCardAdapter(this, R.layout.project_line_item, projects);
        lv.setAdapter(adapter);
        lv.setVerticalScrollBarEnabled(false);
        lv.setDivider(null);
        lv.setSelector(android.R.color.transparent);

        // add footer view
        lv.addFooterView(LayoutInflater.from(this).inflate(R.layout.list_footer, lv, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_license) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.license_title);
            builder.setMessage(R.string.license_message);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ProjectCardAdapter extends ArrayAdapter {
        List<Project> projects;
        int resource;
        int mAnimatedPosition = ListView.INVALID_POSITION;

        private class ViewHolder {
            ImageView pj_image;
            ProjectStatusView pj_status;
            TextView pj_name ;
            TextView pj_endDate;

            public ViewHolder(View view) {
                this.pj_image = (ImageView) view.findViewById(R.id.pj_image);
                this.pj_status = (ProjectStatusView) view.findViewById(R.id.pj_status);
                this.pj_name = (TextView) view.findViewById(R.id.pj_name);
                this.pj_endDate = (TextView) view.findViewById(R.id.pj_endDate);
            }
        }

        public ProjectCardAdapter(Context context, int resource, List<Project> projects) {
            super(context, resource, projects);
            this.projects = projects;
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Inflate Project Card View
            final Project _project = projects.get(position);

            // Set ViewHolder if null
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = getLayoutInflater().inflate(resource, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if (position == projects.size() - 1) {
                // Create New Project Block
                holder.pj_status.setStatus(0);
                holder.pj_status.invalidate();
                holder.pj_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ProjectEditActivity.class);

                        // Maximum ID plus 1
                        _project.setProjectID(projectDao.getLastID() + 1);
                        i.putExtra("Project", _project);
                        v.getContext().startActivity(i);
                    }
                });

                holder.pj_image.setImageResource(R.drawable.image);
                holder.pj_name.setText("+ Project");
            } else {
                // Existing Project Block
                String imagePath = _project.getImagePath();

                // Set a project image
                if (imagePath != null) {
                    Bitmap bmp;
                    Uri uri = Uri.parse(imagePath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            holder.pj_image.setImageBitmap(bmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String[] columns = { MediaStore.Images.Media.DATA };
                        Cursor c = getContentResolver().query(uri, columns, null, null, null);
                        c.moveToFirst();
                        int index = c.getColumnIndex(MediaStore.Images.Media.DATA);
                        String path = c.getString(index);
                        bmp = BitmapFactory.decodeFile(path);
                        holder.pj_image.setImageBitmap(bmp);
                    }
                } else {
                    holder.pj_image.setImageResource(R.drawable.image);
                }

                holder.pj_status.setStatus(_project.getStatus());
                holder.pj_status.invalidate();
                holder.pj_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ShowProjectActivity.class);
                        i.putExtra("Project", _project);
                        v.getContext().startActivity(i);
                    }
                });

                holder.pj_status.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent i = new Intent(v.getContext(), ProjectEditActivity.class);
                        i.putExtra("Project", _project);
                        v.getContext().startActivity(i);
                        return true;
                    }
                });

                holder.pj_name.setText(_project.getName());

                DateTime edt = new DateTime().withMillis(_project.getEndDate());
                holder.pj_endDate.setText(edt.toString(DateTimeFormat.forPattern("MM/dd")));
            }

            if (mAnimatedPosition < position) {
                Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.card_slide_in);
                animator.setTarget(convertView);
                animator.start();
                mAnimatedPosition = position;
            }

            return convertView;
        }
    }
}
