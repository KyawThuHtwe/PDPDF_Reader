package com.cu.pdfreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    public static ArrayList<File> fileList=new ArrayList<>();
    PDFAdapter adapter;
    public static int REQUEST_PERMISSION=1;
    boolean boolean_permission;
    File dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        dir=new File(Environment.getExternalStorageDirectory().toString());
        permission_fn();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                permission_fn();
            }
        },1000);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),ViewPDFiles.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                actionAlertDialog(position);
                return true;
            }
        });
    }
    public void actionAlertDialog(final int position){
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            View view = LayoutInflater.from(this).inflate(R.layout.action_layout, (LinearLayout)findViewById(R.id.layout));
            builder.setView(view);
            TextView name=view.findViewById(R.id.name);
            name.setText(MainActivity.fileList.get(position).getName());
            TextView rename=view.findViewById(R.id.rename);
            TextView delete=view.findViewById(R.id.delete);
            final AlertDialog dialog = builder.create();
            dialog.show();
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameFileAlertDialog(MainActivity.fileList.get(position));
                    dialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelete(MainActivity.fileList.get(position));
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    public void confirmDelete(final File file){
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to Delete?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    file.delete();
                    permission_fn();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void renameFileAlertDialog(final File file){
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            final View view = LayoutInflater.from(this).inflate(R.layout.rename_layout, (LinearLayout)findViewById(R.id.layout));
            builder.setView(view);
            final EditText editText=view.findViewById(R.id.edit);
            editText.setText(file.getName());
            final TextView ok=view.findViewById(R.id.ok);
            TextView cancel=view.findViewById(R.id.cancel);
            final AlertDialog dialog = builder.create();
            dialog.show();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(editText.getText().toString().endsWith(".pdf")) {
                            //dir = new File(Environment.getExternalStorageDirectory().toString());
                            File old = new File(file.getPath() + "");
                            boolean res = old.renameTo(new File( file.getParent(), editText.getText() + ""));
                            if (res) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Rename : "+editText.getText() + "", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Not changed : " + file.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            editText.setText(editText.getText()+".pdf");
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    permission_fn();

                }
            });
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    String sorting="date";
    private void sortAlertDialog() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            View view = LayoutInflater.from(this).inflate(R.layout.sort_layout, (LinearLayout)findViewById(R.id.layout));
            builder.setView(view);
            RadioGroup radioGroup=view.findViewById(R.id.radioGroup);
            final RadioButton date=view.findViewById(R.id.date);
            final RadioButton name=view.findViewById(R.id.name);
            if(getSorting().equals("date")){
                date.setChecked(true);
            }else if(getSorting().equals("name")){
                name.setChecked(true);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.date:
                            sorting="date";break;
                        case R.id.name:
                            sorting="name";break;
                    }
                }
            });
            Button ok = view.findViewById(R.id.ok);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifySorting(sorting);
                    dialog.dismiss();
                }
            });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
    private void verifySorting(String sort) {
        SharedPreferences sharedPreferences=getSharedPreferences("Sorting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Sorting",sort).apply();
    }
    private String getSorting(){
        SharedPreferences sharedPreferences=getSharedPreferences("Sorting", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Sorting","date");
    }
    public void aboutAlertDialog(){
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            View view = LayoutInflater.from(this).inflate(R.layout.about_layout, (LinearLayout)findViewById(R.id.layout));
            builder.setView(view);
            Button ok = view.findViewById(R.id.close);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    String result="horizontal";

    private void orientationAlertDialog() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            View view = LayoutInflater.from(this).inflate(R.layout.orientation_layout, (LinearLayout)findViewById(R.id.layout));
            builder.setView(view);
            RadioGroup radioGroup=view.findViewById(R.id.radioGroup);
            final RadioButton horizontal=view.findViewById(R.id.horizontal);
            final RadioButton vertical=view.findViewById(R.id.vertical);
            if(getVerify().equals("horizontal")){
                horizontal.setChecked(true);
            }else if(getVerify().equals("vertical")){
                vertical.setChecked(true);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.horizontal:
                            result="horizontal";break;
                        case R.id.vertical:
                            result="vertical";break;
                    }
                }
            });
            Button ok = view.findViewById(R.id.verify);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verify(result);
                    dialog.dismiss();
                }
            });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private void verify(String result) {
        SharedPreferences sharedPreferences=getSharedPreferences("Orientation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Orientation",result).apply();
    }
    private String getVerify(){
        SharedPreferences sharedPreferences=getSharedPreferences("Orientation", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Orientation","horizontal");
    }

    private void permission_fn() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION);
            }
        }else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
            }
        } else {
            boolean_permission=true;
            if(fileList.size()>0){
                fileList.clear();
            }
            getfile(dir);
            /*List<File> files = getfile(dir);
            Collections.sort(files, new Comparator<File>() {

                @Override
                public int compare(File file1, File file2) {
                    long k = file1.lastModified() - file2.lastModified();
                    if(k > 0){
                        return 1;
                    }else if(k == 0){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            });

             */

            adapter=new PDFAdapter(getApplicationContext(),fileList);
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                boolean_permission=true;
                getfile(dir);
                adapter=new PDFAdapter(getApplicationContext(),fileList);
                listView.setAdapter(adapter);
            }else {
                Toast.makeText(getApplicationContext(),"Please allow the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public ArrayList<File> getfile(File dir){
        File listFile[]=dir.listFiles();
        if(listFile!=null && listFile.length>0){
            for(int i=0;i<listFile.length;i++){
                if(listFile[i].isDirectory()){
                    getfile(listFile[i]);
                }else {
                    boolean booleanpdf=false;
                    if(listFile[i].getName().endsWith(".pdf")){
                        for(int j=0;j<fileList.size();j++){
                            if(fileList.get(j).getName().equals(listFile[i].getName())){
                                booleanpdf=true;

                            }else {

                            }
                        }
                        if(booleanpdf){
                            booleanpdf=false;
                        }else {
                            fileList.add(listFile[i]);

                        }
                    }
                }
            }
        }
        return fileList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem search_item = menu.findItem(R.id.search);
        SearchView searchView= (SearchView) search_item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)){
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.orientation:
                orientationAlertDialog();
                break;
            case R.id.about:
                aboutAlertDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}