package com.cu.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ViewPDFiles extends AppCompatActivity implements OnPageChangeListener{

    PDFView pdfView;
    int position=-1;
    ActionBar actionBar;
    int mCurrentPage=0;
    int totalPage=0;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_p_d_files);
        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        pdfView=findViewById(R.id.pdfView);
        position=getIntent().getIntExtra("position",-1);
        if(getPdfName(MainActivity.fileList.get(position).getName()).equals(MainActivity.fileList.get(position).getName())){
            mCurrentPage=getPdfPage(MainActivity.fileList.get(position).getName());
        }
        displayPDF();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPDF() {
        if(getVerify().equals("horizontal")){
            {
                pdfView.fromFile(MainActivity.fileList.get(position))
                        .defaultPage(mCurrentPage)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        .password(null)
                        .enableAntialiasing(true)
                        .onPageChange((OnPageChangeListener) this)
                        .enableAnnotationRendering(true)
                        .onPageScroll(new OnPageScrollListener() {
                            @Override
                            public void onPageScrolled(int page, float positionOffset) {
                                // Toast.makeText(getApplicationContext(),"onPageScrolled: page " + page + " positionOffset " + positionOffset,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onRender(new OnRenderListener()
                        {
                            @Override
                            public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight)
                            {
                                pdfView.fitToWidth(mCurrentPage);
                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                totalPage=nbPages;
                                // actionBar.setTitle(MainActivity.fileList.get(position).getName()+"("+mCurrentPage+"/"+nbPages+")");

                                // Toast.makeText(getApplicationContext(),"loadComplete: totalPages " + nbPages,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(getApplicationContext(),"onError",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .scrollHandle(new DefaultScrollHandle(this))
                        .spacing(2)
                        .load();
            }
        }else if(getVerify().equals("vertical")){
            pdfView.fromFile(MainActivity.fileList.get(position))
                    .defaultPage(mCurrentPage)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .password(null)
                    .enableAntialiasing(true)
                    .onPageChange((OnPageChangeListener) this)
                    .enableAnnotationRendering(true)
                    .onPageScroll(new OnPageScrollListener() {
                        @Override
                        public void onPageScrolled(int page, float positionOffset) {
                           // Toast.makeText(getApplicationContext(),"onPageScrolled: page " + page + " positionOffset " + positionOffset,Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onRender(new OnRenderListener()
                    {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight)
                        {
                            pdfView.fitToWidth(mCurrentPage);
                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            totalPage=nbPages;
                           // Toast.makeText(getApplicationContext(),"loadComplete: totalPages " + nbPages,Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(getApplicationContext(),"onError",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(2)
                    .load();
        }

    }
    @Override
    public void onPageChanged(int page, int pageCount)
    {
        mCurrentPage = page;
        //setTitle(String.format("%s %s / %s", "Page Number", page + 1, pageCount));
        int count=page+1;
        actionBar.setTitle(MainActivity.fileList.get(position).getName()+"("+count+"/"+totalPage+")");
        TextFile(MainActivity.fileList.get(position).getName(),MainActivity.fileList.get(position).getName(),mCurrentPage);
    }
    private void TextFile(String Name,String title,int page) {
        SharedPreferences sharedPreferences=getSharedPreferences("PdfFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Name,title);
        editor.putInt(Name+"PdfPage",page);
        editor.apply();
    }
    public String getPdfName(String Name){
        SharedPreferences sharedPreferences=getSharedPreferences("PdfFile", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Name,"");
    }
    public int getPdfPage(String Name){
        SharedPreferences sharedPreferences=getSharedPreferences("PdfFile", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Name+"PdfPage",0);
    }

    private String getVerify(){
        SharedPreferences sharedPreferences=getSharedPreferences("Orientation", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Orientation","horizontal");
    }
}