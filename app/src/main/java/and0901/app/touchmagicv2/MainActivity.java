package and0901.app.touchmagicv2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//フィールド変数
	TextView heroName, txtHeroHP, textEnemyHP, txt4, comment1, comment2, comment3, heroHPView, enemyHPView, txt10;
	Button okBtn;
	String hero1="自分", hero2, enemy1="相手", enemy2;	//名前の設定
	MagicSquare magicSquare = null;
	private String heroHP, enemyHP;
	int order = 0;
	private Handler handler;
	private SurfaceHolder holder;
	private Thread thread;
	int i = 0;
	float width;				//画面サイズ
	float height;				//画面サイズ
	private Activity context;

	public void text1(){
		comment1.setText("");
		comment2.setText("");
		comment3.setText("");
	}


	//////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		heroHP = "100";
		enemyHP = "100";

		//MagicSquareの引数などを参照できるようにする
		magicSquare = (MagicSquare)findViewById(R.id.MagicSquare);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	//初期化に使いたい
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			magicSquare.intHeroHP=400;
			magicSquare.intEnemyHP=400;
			magicSquare.heroHP="400";
			magicSquare.enemyHP="400";
			magicSquare.attack=0;
			magicSquare.attackEnemy=0;
			magicSquare.atk="";
			magicSquare.atkE="";			
			magicSquare.commentTop1="";
			magicSquare.commentTop2="";
			magicSquare.redFlag=0;
			magicSquare.greenFlag=0;
			magicSquare.brownFlag=0;
			magicSquare.blueFlag=0;
			magicSquare.battleFlag=1;
			magicSquare.turnEnemy=0;
			magicSquare.i=0;
			magicSquare.wait=-1;
            magicSquare.blocks.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//ゲッターセッター
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
}
