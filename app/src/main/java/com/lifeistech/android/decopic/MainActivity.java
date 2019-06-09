package com.lifeistech.android.decopic;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    int picnum = 0;
    FrameLayout frame;

    // 画像表示用ImageView
    private ImageView picture;
    // 画像読み込みの際に使用する変数
    private static final int REQUEST_ORIGIN = 0;

    // スタンプの名前を格納する変数
    String stampName;
    // スタンプのImageView
    private ImageView stamp[] = new ImageView[4];

    // ドロップ時のの座標格納用変数
    float x;
    float y;

    // ドラッグしているものがPicture内にあるかチェック
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 結びつけ
        picture = (ImageView) findViewById(R.id.picture);
        for(int i = 0; i < 4; i++){
            stamp[i] = (ImageView)findViewById(getResources().getIdentifier("imageView" + i, "id", getPackageName()));
        }
        frame = (FrameLayout) findViewById(R.id.Frame);

        // タッチしたときの動作をそれぞれのImageViewにセット
        stamp[0].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                // 現在のスタンプを変数にセット
                stampName = "Star";
                // クリップボードに格納
                ClipData clipdata = ClipData.newPlainText("Stamp0", "Drag");
                // ドラッグを開始
                view.startDrag(clipdata, new View.DragShadowBuilder(view), (Object) view, 0);
                return false;
            }
        });

        stamp[1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                stampName = "Heart";
                ClipData clipdata = ClipData.newPlainText("Stamp1", "Drag");
                view.startDrag(clipdata, new View.DragShadowBuilder(view), (Object) view, 0);
                return false;
            }
        });

        stamp[2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                stampName = "Ribon";
                ClipData clipdata = ClipData.newPlainText("Stamp2", "Drag");
                view.startDrag(clipdata, new View.DragShadowBuilder(view), (Object) view, 0);
                return false;
            }
        });

        stamp[3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                stampName = "Note";
                ClipData clipdata = ClipData.newPlainText("Stamp3", "Drag");
                view.startDrag(clipdata, new View.DragShadowBuilder(view), (Object) view, 0);
                return false;
            }
        });

        picture.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    // 領域外にドラッグした場合
                    case DragEvent.ACTION_DRAG_EXITED:
                        flag = false;
                        break;
                    // 領域内でドロップした場合
                    case DragEvent.ACTION_DROP:
                        x = dragEvent.getX();
                        y = dragEvent.getY();
                        break;
                    // 領域内に入った場合
                    case DragEvent.ACTION_DRAG_ENTERED:
                        flag = true;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // ドラッグした時の各スタンプの動作
        for(int i = 0; i < 4; i++){
            stamp[i].setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    // ドラッグが終了したとき
                    if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED){
                        // Pictureの内部にあるとき
                        if(flag){
                            // どのスタンプを選択中かで追加するものを変える
                            switch (stampName){
                                case "Star":
                                    addView(0);
                                    break;
                                case "Heart":
                                    addView(1);
                                    break;
                                case "Ribon":
                                    addView(2);
                                    break;
                                case "Note":
                                    addView(3);
                                    break;
                                default:
                                    break;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
        }
    }

    // レイアウト上に追加する
    public void addView(int stampNum){
        // スタンプのサイズを設定
        FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(180,180);
        // ImageViewを追加
        ImageView imgV = new ImageView(getApplicationContext());
        // ImageViewにドロップした画像を設定
        imgV.setImageResource(getResources().getIdentifier("stamp" + stampNum, "drawable", getPackageName()));

        // ImageViewをレイアウトに追加
        frame.addView(imgV, params);

        // ImageViewの位置を設定
        imgV.setTranslationX((float) (x - ((stamp[stampNum].getWidth()) / 2.0)));
        imgV.setTranslationY((float) (y - ((stamp[stampNum].getWidth()) / 2.0)));
    }

    // 画像選択ボタンを押した時の動作
    public void select(View v){
        // インテント
        Intent intent = new Intent();
        // ファイルのタイプに画像を指定
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_ORIGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 正しく選択された場合
        if(resultCode == RESULT_OK){
            try{
                // 選択したデータを入力
                InputStream is = getContentResolver().openInputStream(data.getData());
                // bitmap形式にデコード
                Bitmap img = BitmapFactory.decodeStream(is);
                // ImageViewに設定
                picture.setImageBitmap(img);
                // ストリームを閉じる
                is.close();
            } catch (Exception e) {}
        }
    }

    // 保存するメソッド
    public void save() throws Exception {
        try {
            // bmp画像を作成
            frame.setDrawingCacheEnabled(true);
            Bitmap save_bmp = Bitmap.createBitmap(frame.getDrawingCache());
            // pathを指定
            String folderpath = Environment.getExternalStorageDirectory() + "/DecoPic/";
            // 上記pathのフォルダと結びついた変数を作成
            File folder = new File(folderpath);
            // フォルダが無い時
            if (!folder.exists()) {
                // フォルダを作る
                folder.mkdirs();
            }
            // 上記フォルダの下にあるsampleN.pngファイル結びついた変数を作成
            File file = new File(folderpath, "sample" + picnum + ".png");
            // 既にあれば変数をsampleN+1pngファイルと結びつける
            if (file.exists()) {
                for (; file.exists(); picnum++) {
                    file = new File(folderpath, "sample" + picnum + ".png");
                }
            }
            // sampleN.pngファイルに出力
            FileOutputStream outStream = new FileOutputStream(file);
            // bmpファイルで出力
            save_bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            // 作成したことを表すメッセージ表記
            Toast.makeText(
                    getApplicationContext(),
                    "Image saved",
                    Toast.LENGTH_SHORT).show();
            frame.setDrawingCacheEnabled(false);
            showFolder(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // イメージファイルが保存されたことを通知するメソッド
    private void showFolder(File path) throws Exception {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = getApplicationContext()
                    .getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_MODIFIED,
                    System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, path.length());
            values.put(MediaStore.Images.Media.TITLE, path.getName());
            values.put(MediaStore.Images.Media.DATA, path.getPath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            throw e;
        }
    }

    // メニューを作るメソッド
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // メニューのボタンが押された時に呼ばれるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save){
            try{
                save();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
