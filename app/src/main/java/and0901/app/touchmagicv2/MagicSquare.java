package and0901.app.touchmagicv2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MagicSquare extends View {

	//画像の読み込み
	Resources res = this.getContext().getResources();
//	Bitmap dq1hero = BitmapFactory.decodeResource(res, R.drawable.dq1hero);
//	Bitmap dq2hero1 = BitmapFactory.decodeResource(res, R.drawable.dq2hero1);
//	Bitmap dq2hero2 = BitmapFactory.decodeResource(res, R.drawable.dq2hero2);
	Bitmap monster1 = BitmapFactory.decodeResource(res, R.drawable.ryu_ou);
//	Bitmap monster2 = BitmapFactory.decodeResource(res, R.drawable.monster2size500);
	Bitmap haikeiBattleA = BitmapFactory.decodeResource(res, R.drawable.a201208182021);
	Bitmap haikeiBattleB = BitmapFactory.decodeResource(res, R.drawable.b201208182037);
	//音楽の読み込み
//	MediaPlayer battle1 = MediaPlayer.create(getContext(), R.raw.dq1_battle1);
//	SoundPool damage1 = new SoundPool(5, AudioManager.STREAM_MUSIC,0);

	//フィールド変数---その他
	Context context;
	MainActivity ma;
	private Paint paint;
	private float width, height;
	private float x, y, xrd, yrd, xgr, ygr, xbr, ybr, xbl, ybl;    //タッチイベントでの座標取得用
	int n = 4;                    //チェックポイントを通れる回数
	ArrayList<String> al1 = new ArrayList<String>();            //ポイント追加用---毎回リセットされる
	ArrayList<String> al2 = new ArrayList<String>();            //アクティビティに飛ばす用のal1コピー
	//グラフィック関係
	float grid;
	//フラグ関係
	int flag = 0, msFlag = 0, bmFlag = 0, bcFlag = 0,                    //ms内に入ったらフラグ
			redFlag = 0, greenFlag = 0, brownFlag = 0, blueFlag = 0,//円に入ったらフラグ
			lock1 = 0, lock2 = 0,                                //ロック
			touchNext = 0;                                    //タッチして次に進ませるモード
	int movePtn = 0;                            //入力結果によってテキストと行動を選択する場合に必要
	int waitFlag = 0;                            //
	int battleFlag = 1;
	int turnEnemy = 0;                //敵のターンに入る際にロック
	//フィールド変数---サイズ関係
	int xStatus, yStatus;                    //BattleStatus
	int xSquare, ySquare;                    //BattleSquare
	int xMenu, yMenu;                        //BattleMenu
	int xComm, yComm;                        //BattleComment
	private float cRadius, c1x, c2x, c3x, c4x, c1y, c2y, c3y, c4y;    //各サークルの大きさと座標
	private float cCenter;                                    //サークルの中心点
	private float blockLine1, blockLine2;                        //上部中部下部の区切り位置
	private float multiple1 = 0f, multiple2 = 0f;
	private double multi1 = 0d, multi2 = 0d;
	//バトル関連
	int intHeroHP = 400, intEnemyHP = 400;                //DBから読んでくる、自分と相手のLVに応じたHP。
	int backHeroHP, backEnemyHP;            //計算用のHP
	String heroHP = "400", enemyHP = "400";            //intHeroHP、intEnemyHPを入れる。
	int wait = 0, waitCalc = 0, waitEnemy = 0, waitTotal = 0;    //入力から行動実施までの待ち時間
	private String f = null;                //入力パターン表示用
	int attack = 0, attackEnemy = 0;        //攻撃力
	private int attribute, attributeEnemy;    //属性
	String commentTop1 = "", commentTop2 = "", comment1 = "", commentHero = "", commentEnemy = "";//行動コメント
	private Random rnd;                        //敵行動パターン決定用乱数
	private String moveEnemy;                //敵行動パターン用
	private String[] e1, e2, e3;            //敵行動パターン配列
	private Object iii;
	String atk = "", atkE = "";
	int i = 0;
	//浮き上がるブロック
	ArrayList<Block> blocks = new ArrayList<Block>();
	ArrayList<Block> blocks3 = new ArrayList<Block>();
	ArrayList<Block> blocks2 = new ArrayList<Block>();
	private Block ss;
	private String sss;
	private int count;
	private int eWidth, eHeight;
	//アニメーションのフラグ
	private int eDamage;
	private int soundID1;
	private int streamID1;

	//コンストラクタ
	public MagicSquare(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
		init(context);
	}

	//1- Viewを利用するために必要な初期化処理
	//コンストラクタから呼ばれる
	private void init(Context context) { //-1-
		this.context = context;
		this.paint = new Paint();
		paint.setAntiAlias(true);        //アンチエイリアス有効
		onWindowFocusChanged(true);        //1-1 Viewのサイズを取得
		battleFlag = 1;                    //０になるとバトル終了し、フィールド画面へ
		heroHP = "400";    //初期値
		enemyHP = "400";
		comment1 = "入力してください";

		//画像サイズの取得
		eWidth = monster1.getWidth();
		eHeight = monster1.getHeight();
		//サウンドプールの読み込み
//		soundID1 = damage1.load(context,R.raw.nc43638damage1,1);
		//BGM再生
		//battle1.setLooping(true);
		//battle1.start();

		//機種の画面サイズを取得
		//WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
		//Display disp = 	wm.getDefaultDisplay();
		//width = disp.getWidth();
		//height= disp.getHeight();
	}

	//1-1 Viewのサイズを取得
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO 自動生成されたメソッド・スタブ
		super.onWindowFocusChanged(hasWindowFocus);

		width = findViewById(R.id.MagicSquare).getWidth();
		height = findViewById(R.id.MagicSquare).getHeight();
	}

	//2- Viewに描画
	@Override
	protected void onDraw(Canvas canvas) {    //-2-
		// TODO 自動生成されたメソッド・スタブ
		super.onDraw(canvas);

		//バトルに入っている場合
		switch (battleFlag) {
			case 1:
////////////３つのブロックの境界線
				blockLine1 = height / 20 * 3;
				blockLine2 = height / 7 * 6;
				//canvasを塗りつぶす
				switch (touchNext) {
					case 0:
						canvas.drawColor(Color.rgb(255, 245, 238));
						break;
					case 1:    //敵のターンが続く場合
						canvas.drawColor(Color.GRAY);
						break;
				}
////////////ステータス
				paint.setColor(Color.argb(200,0,0,0));
				canvas.drawRect(width/4, height/80*0, width/40*30, height/80*10, paint);        //プレイヤー枠
				canvas.drawRect(width/4, height/80*60, width/40*30, height/80*70, paint);    //敵枠
				//HP／MP
				paint.setTextSize(width / 15);
				paint.setColor(Color.rgb(135, 206, 250));
				canvas.drawText("HP: " + heroHP, width/40*11, height/80*4, paint);
				canvas.drawText("HP: " + enemyHP, width/40*11, height/80*64, paint);
				//ダメージ量
				paint.setColor(Color.MAGENTA);
				canvas.drawText(atkE, width/40*26, height/80*64, paint);    //プレイヤーのダメージ
				canvas.drawText(atk , width/40*26, height/80*64, paint);    //敵のダメージ
				//コメント
				paint.setTextSize(width / 15);
				paint.setColor(Color.WHITE);
				canvas.drawText(commentTop1, width/40*11, height/80*9, paint);
				canvas.drawText(commentTop2, width/40*11, height/80*69, paint);
////////////メイン
				//グラフィック
				grid = width / 18;
				float gw = width - (float)eWidth;
				float gh = height - (float)eHeight;
//			canvas.drawBitmap(haikeiBattleA, -100, height/5, paint);		//背景
//			canvas.drawBitmap(monster2, (width/6)*2.5f, blockLine1, paint);	//悪魔
			canvas.drawBitmap(monster1, gw/2, gh/2, paint);		//獣人
				//サークルの設定
				cRadius = grid * 1.5f;                            //円の半径
				cCenter = cRadius / 2;                            //サークルの中心
				c1x = grid * 3.5f;
				c1y = blockLine1 + grid * 3.5f;    //あか座標
				c2x = grid * 14.5f;
				c2y = blockLine1 + grid * 3.5f;    //みどり座標
				c3x = grid * 3.5f;
				c3y = blockLine1 + grid * 14.5f;    //ちゃ座標
				c4x = grid * 14.5f;
				c4y = blockLine1 + grid * 14.5f;    //あお座標
				//グリッドの描画
//				paint.setColor(Color.rgb(32, 178, 170));        //線の色
//				paint.setStrokeWidth((width / width) * 3);            //線の太さ
//				for (int i = 0; i <= 18; i++) {
//					canvas.drawLine(grid * i, blockLine1, grid * i, blockLine1 + width, paint);
//					canvas.drawLine(0, blockLine1 + grid * i, width, blockLine1 + grid * i, paint);
//				}
//				paint.setColor(Color.rgb(208, 32, 144));
//				canvas.drawLine(0, blockLine1 + grid * 9, width, blockLine1 + grid * 9, paint);
//				canvas.drawLine(width / 2, blockLine1, width / 2, blockLine2, paint);
				//浮き上がるブロック描画
				paint.setColor(Color.rgb(47, 79, 79));
				//アレイリストblocksの内容を
				for (int i = 0; i < blocks3.size(); i++) {
					for (Block b : blocks3) {
						double x1;
						double x2;
						x1 = b.getD1();
						x2 = b.getD2();
						canvas.drawRect((float)x1, blockLine1 + (float) x2, (float) x1 + grid, blockLine1 + (float) x2 + grid, paint);
						//canvas.drawRect(r, paint);
					}
				}
				//サークル描画
				paint.setColor(Color.argb(200,255,0,0));
				if (touchNext != 0)
					paint.setColor(Color.LTGRAY);
				canvas.drawCircle(c1x, c1y, cRadius, paint);    //あか
				paint.setColor(Color.argb(200, 64, 224, 208));
				if (touchNext != 0)
					paint.setColor(Color.LTGRAY);
				canvas.drawCircle(c2x, c2y, cRadius, paint);    //みどり
				paint.setColor(Color.argb(200, 139, 69, 19));
				if (touchNext != 0)
					paint.setColor(Color.LTGRAY);
				canvas.drawCircle(c3x, c3y, cRadius, paint);    //ちゃ
				paint.setColor(Color.argb(200,0,0,255));
				if (touchNext != 0)
					paint.setColor(Color.LTGRAY);
				canvas.drawCircle(c4x, c4y, cRadius, paint);    //あお
////////////コメント
				//枠線
//				paint.setColor(Color.WHITE);
//				RectF rect = new RectF(0, blockLine2, width, height);
//				canvas.drawRoundRect(rect, width / 70, width / 70, paint);
//				paint.setColor(Color.BLACK);
//				RectF rect2 = new RectF(width / 100, blockLine2 + width / 100, width - width / 100, height);
//				canvas.drawRoundRect(rect2, width / 70, width / 70, paint);
				paint.setColor(Color.WHITE);
				paint.setTextSize(width / 18);
				//バトル終了してフィールドの描画
				//if(battleFlag!=1){
				//canvas.drawText(comment1, grid, blockLine2+grid, paint);
				//}
				canvas.drawText(comment1, grid, blockLine2 + grid, paint);
				//パターン入力後、下部にテキストを表示、上部のHP／MPの増減を表示。

				//敵ダメージの場合
				if(eDamage!=0){
					invalidate();
					try {
						Thread.sleep(200);
					}catch (InterruptedException e){

					}
				}
				break;
			default:
				break;

		}
	}


	//指を動かし、チェックポイントに触れたら１ポイントが配列に入る
	//「キャンセル」か「OK」を押すと終了する
	@Override
	public boolean onTouchEvent(MotionEvent event) {    //-3-
		// TODO 自動生成されたメソッド・スタブ
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
//				damage1.stop(streamID1);
//				damage1.release();
				blocks.clear();
				blocks2.clear();
				blocks3.clear();
				count = 0;
				if (touchNext != 1) {
					//タッチの座標を取得
					x = event.getX();
					y = event.getY();
					al1.clear();
					f = "";
				}
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				//タッチの座標を取得
				x = event.getX();
				y = event.getY();
				//プレイヤーターンが続く場合
				if (touchNext != 1) {
					//MagicSquareに入った場合
					if (x >= 0 && x <= height && y > blockLine1 && y < blockLine2) {
						//タッチの座標を取得--赤
						xrd = event.getX();
						yrd = event.getY();
						//タッチの座標を取得--緑
						xgr = event.getX();
						ygr = event.getY();
						//タッチの座標を取得--茶
						xbr = event.getX();
						ybr = event.getY();
						//タッチの座標を取得--青
						xbl = event.getX();
						ybl = event.getY();

						battleSquare(); //-4-

						//浮き上がるブロック
						//x=gridのいくつ分かを調べる
						multiple1 = x / grid;
						double dX = Math.ceil(multiple1);    //小数点切り上げ
						multiple2 = y - blockLine1;
						multiple2 = multiple2 / grid;
						double dY = Math.ceil(multiple2);
//						Log.v("test", "x= " + Double.toString(dX));
//						Log.v("test", "y= " + Double.toString(dY));
						multi1 = Math.floor(multiple1);     //小数点切り捨て
						multi1 = multi1 * grid;
						multi2 = Math.floor(multiple2);
						multi2 = multi2 * grid;

						blocks2.add(new Block(multi1, multi2));

						double a = 0d;
						double b = 0d;

						Log.v("test", "multiple = " + multiple1 + ", " + multiple2);
						Log.v("test", "multi = " + multi1 + ", " + multi2);
//					Iterator<Block> i = blocks.iterator();
//					Iterator<Block2> i2 = blocks2.iterator();
//
//					while (i.hasNext()) {
//						if (i.equals(0)) {
//							blocks.add(new Block(multi1, multi2));
//							invalidate();
//							for (Block2 b2 : blocks2) {
//								a = b2.getD1();
//								b = b2.getD1();
//							}
//						} else {
//						}
//					}
//					while (i.hasNext()) {
//						for (Block bl : blocks) {
//
//						}
//					}
//				}
//			}

						//blocksの要素が０なら
						//---blocks  = 追加予定のブロック座標リスト
						//---blocks2 = 比較用のブロック座標 （常に１つ）
						//---blocks3 = 最終的に表示するブロック座標リスト

						//タッチを話すまで初期化されないので表示用blocks3を比較用blocksにコピー
						for(Block bb : blocks3){
							double dd = bb.getD1();
							double ddd= bb.getD2();
							blocks.clear();
							blocks.add(new Block(dd, ddd));
						}

						if (blocks.size() < 1) {
							blocks.add(new Block(multi1, multi2));
							blocks3.add(new Block(multi1, multi2));
							Log.v("test", "ブロック表示開始");
						} else if (blocks.size() > 0) {
							//blocksの要素が１以上なら
							for (Block b2 : blocks2) {
								a = Math.floor(b2.getD1());
								b = Math.floor(b2.getD1());
								for (Block blck : blocks) {
									double a3 = Math.floor(blck.getD1());
									double b3 = Math.floor(blck.getD2());
//							for (int i=0; i < blocks.size(); i++) {
//								double a3 = blocks.get(i).getD1();
//								double b3 = blocks.get(i).getD2();

									if (Math.floor(a/grid) != Math.floor(a3/grid) && Math.floor(b/grid) != Math.floor(b3/grid)) {
										blocks3.add(new Block(multi1, multi2));
										Log.v("test", "blocks  = " + Math.floor(a3/grid) + ", " + Math.floor(b3/grid));
										Log.v("test", "blocks2 = " + Math.floor(a/grid) + ", " + Math.floor(b/grid));
									}else if(Math.floor(a/grid) == Math.floor(a3/grid) && Math.floor(b/grid) == Math.floor(b3/grid)){
										count += 1;
										Log.v("test", "c blocks  = " + Math.floor(a3/grid) + ", " + Math.floor(b3/grid));
										Log.v("test", "c blocks2 = " + Math.floor(a/grid) + ", " + Math.floor(b/grid));
									}
								}
							}
						}
					}
				}
				invalidate();

				//ログ用
				Log.v("test", "blocks  = " + Integer.toString(blocks.size()));
				Log.v("test", "blocks2 = " + Integer.toString(blocks2.size()));
				Log.v("test", "blocks3 = " + Integer.toString(blocks3.size()));
				Log.v("test", "count = " + count);

				blocks2.clear();

				break;
			case MotionEvent.ACTION_UP:
				//敵ターンかどうか
				switch (touchNext) {
					//プレイヤーターン
					case 0:
						//４属性の入力が完了しているか確認
						if (al1.size() == 4 || al1.size() == 1) {        //パターンが入力された場合
							//トースト---al1の状態を表示
							for (String f1 : al1) {
								if (f != "") {
									f += "," + f1;
								} else {
									f1 += "";
									f = f1;
								}
							}
							Toast.makeText(getContext(), f, Toast.LENGTH_SHORT).show();
							//入力結果と行動パターンの選択とその結果
							if (wait < 1) {
								movePattern();        //-5-
								if (waitEnemy < 1)
									movePatternEnemy();    //-6-
								moveCalc();            //-7-
//								if(eDamage!=0)
//									streamID1 = damage1.play(soundID1,5,5,0,0,1.0f);
							}
							comment1 = "";
						} else {
							commentTop1 = "";
							comment1 = "入力ミスです";
						}
						break;
					//敵ターン
					case 1:
						touchNext = 0;
						atk = "";
						atkE = "";
						commentTop1 = "";
						commentEnemy = "";
						movePatternEnemy();
						moveCalc();
						break;
				}

				//ロック解除
				msFlag = 0;
				bmFlag = 0;
				redFlag = 0;
				greenFlag = 0;
				brownFlag = 0;
				blueFlag = 0;
				//結果を再表示
				invalidate();        //-2-

//				//blocks
//				for (Block blck : blocks2) {
//					blocks.add(blck);
//				}
				Log.v("test", "/////");


				break;
			default:
				break;
		}
		return true;
	}


	//真ん中
	public void battleSquare() {    //-4-
		if (al1.size() < 4) {    //al1のサイズを決める変数（今は4）で使える属性の数を制限する
			//あか
			if (redFlag != 1 && xrd >= c1x - cRadius && xrd <= c1x + cRadius && yrd >= c1y - cRadius && yrd <= c1y + cRadius) {
				redFlag = 1;                            //フラグを立てて
				msFlag = 1;                                //ms内の再表示用フラグ	ひつよう？
				al1.add("赤");                            //アレイリストal1に追加
			}
			//円から出たらフラグ解除
			if (redFlag != 0 && xrd < c1x - cRadius || xrd > c1x + cRadius || yrd < c1y - cRadius || yrd > c1y + cRadius) {
				msFlag = 0;
			}
			//みどり
			if (greenFlag != 1 && xgr >= c2x - cRadius && xgr <= c2x + cRadius && ygr >= c2y - cRadius && ygr <= c2y + cRadius) {
				if (greenFlag != 1) {
					greenFlag = 1;
					msFlag = 1;
					al1.add("緑");
				}
			}
			if (greenFlag != 0 && xgr < c2x - cRadius || xgr > c2x + cRadius || ygr < c2y - cRadius || ygr > c2y + cRadius) {
				msFlag = 0;
			}
			//ちゃ
			if (brownFlag != 1 && xbr >= c3x - cRadius && xbr <= c3x + cRadius && ybr >= c3y - cRadius && ybr <= c3y + cRadius) {
				if (brownFlag != 1) {
					brownFlag = 1;
					msFlag = 1;
					al1.add("茶");
				}
			}
			if (brownFlag != 0 && xbr < c3x - cRadius || xbr > c3x + cRadius || ybr < c3y - cRadius || ybr > c3y + cRadius) {
				msFlag = 0;
			}
			//あお
			if (blueFlag != 1 && xbl >= c4x - cRadius && xbl <= c4x + cRadius && ybl >= c4y - cRadius && ybl <= c4y + cRadius) {
				blueFlag = 1;
				msFlag = 1;
				al1.add("青");
			}
			if (blueFlag != 0 && xbl < c4x - cRadius || xbl > c4x + cRadius || ybl < c4y - cRadius || ybl > c4y + cRadius) {
				msFlag = 0;
			}
		}
	}

	private void battleComment() {
	}

	private void battleMenu() {
	}

	//行動パターン---プレイヤー
	void movePattern() {            //-5-
		//基本メニューセレクト
		if (f.equals("赤")) {    //たたかう
			movePtn = 2;
			attack = 5;
			wait = 2;
			commentHero = "こうげき！";
		} else if (f.equals("緑")) {    //ふせぐ
			movePtn = 2;
			attack = 0;
			wait = 1;
			commentHero = "みがまえた！";
		} else if (f.equals("茶")) {    //どうぐ
			movePtn = 3;
			attack = 0;
			wait = 1;
			commentHero = "";
		} else if (f.equals("青")) {    //にげる
			movePtn = 4;
			attack = 0;
			wait = 0;
			commentHero = "にげだした！";
			//あかスタート
		} else if (f.equals("赤,緑,青,茶")) {
			movePtn = 111;
			wait = 3;
			attack = 10;
			attribute = 1;
			commentHero = "メラ！";
		} else if (f.equals("赤,緑,茶,青")) {
			movePtn = 112;
			wait = 5;
			attack = 20;
			attribute = 1;
			commentHero = "メラミ！";
		} else if (f.equals("赤,茶,青,緑")) {
			movePtn = 121;
			wait = 8;
			attack = 30;
			attribute = 1;
			commentHero = "メラゾーマ！";
		} else if (f.equals("赤,茶,緑,青")) {
			movePtn = 122;
			wait = 3;
			attack = 50;
			attribute = 1;
			commentHero = "ギラ！";
		} else if (f.equals("赤,青,緑,茶")) {
			movePtn = 131;
			wait = 7;
			attack = 150;
			attribute = 1;
			commentHero = "ベギラマ！";
		} else if (f.equals("赤,青,茶,緑")) {
			movePtn = 132;
			wait = 9;
			attack = 300;
			attribute = 1;
			commentHero = "ベギラゴン！";
			//みどりスタート
		} else if (f.equals("緑,青,茶,赤")) {
			movePtn = 211;
			wait = 3;
			attack = 10;
			attribute = 2;
			commentHero = "バギ！";
		} else if (f.equals("緑,青,赤,茶")) {
			movePtn = 212;
			wait = 6;
			attack = 25;
			attribute = 2;
			commentHero = "バギマ！";
		} else if (f.equals("緑,茶,赤,青")) {
			movePtn = 213;
			wait = 3;
			attack = 50;
			attribute = 2;
			commentHero = "バギクロス！";
		} else if (f.equals("緑,茶,青,赤")) {
			movePtn = 214;
			wait = 3;
			attack = 25;
			attribute = 2;
			commentHero = "ホイミ！";
		} else if (f.equals("緑,赤,茶,青")) {
			movePtn = 215;
			wait = 3;
			attack = 70;
			attribute = 2;
			commentHero = "ベホイミ！";
		} else if (f.equals("緑,赤,青,茶")) {
			movePtn = 216;
			wait = 3;
			attack = 150;
			attribute = 2;
			commentHero = "ベホマ！";
			//ちゃスタート
		} else if (f.equals("茶,赤,緑,青")) {

		} else if (f.equals("茶,赤,青,緑")) {

		} else if (f.equals("茶,緑,赤,青")) {

		} else if (f.equals("茶,緑,青,赤")) {

		} else if (f.equals("茶,青,赤,緑")) {

		} else if (f.equals("茶,青,緑,赤")) {

			//あおスタート
		} else if (f.equals("青,茶,赤,緑")) {

		} else if (f.equals("青,茶,緑,赤")) {

		} else if (f.equals("青,赤,緑,茶")) {

		} else if (f.equals("青,赤,茶,緑")) {

		} else if (f.equals("青,緑,赤,茶")) {

		} else if (f.equals("青,緑,茶,赤")) {

		} else {
			commentHero = "入力ミス";
		}
	}

	//行動パターン---敵
	void movePatternEnemy() {    //-6-
		rnd = new Random();
		int ran = rnd.nextInt(3);
		//敵行動パターン選択
		switch (i) {
			case 0:
				e1 = new String[]{"茶", "青", "緑", "赤"};
				waitEnemy = 4;
				commentEnemy = "クエイク！";
				attackEnemy = 5;
				attributeEnemy = 3;
				i += 1;
				break;
			case 1:
				e2 = new String[]{"茶", "青", "赤", "緑"};
				waitEnemy = 3;
				commentEnemy = "グラビデ！";
				attackEnemy = 10;
				attributeEnemy = 3;
				i += 1;
				break;
			case 2:
				e3 = new String[]{"茶", "緑", "赤", "青"};
				waitEnemy = 4;
				commentEnemy = "フレア！";
				attackEnemy = 20;
				attributeEnemy = 3;
				i -= 2;
				break;
		}
	}


	//HP、waitを計算
	void moveCalc() {    //-7-
		//敵の攻撃ターン
		if (wait > waitEnemy) {
			//プレイヤーの残りHP
			intHeroHP = intHeroHP - attackEnemy;
			//ダメージ量
			atkE = "- " + Integer.toString(attackEnemy);
			atk = "";
			//プレイヤーのコメント欄---残りwait数
			wait = wait - waitEnemy;
			commentTop1 = "wait... " + Integer.toString(wait);
			commentTop2 = commentEnemy;
			//敵のターンが来たのでwait数を０にする
			waitEnemy = 0;
			//敵ターン終了後にタッチで進めるためのフラグ
			touchNext = 1;
			//下部コメント
			comment1 = "画面をタッチしてください";
			//ダメージアニメーションフラグ
			eDamage = 1;
			//自分の攻撃ターン
		} else if (wait < waitEnemy) {
			//敵の残りHP
			intEnemyHP = intEnemyHP - attack;
			//ダメージ量
			atkE = "";
			atk = "- " + Integer.toString(attack);
			//敵のコメント欄--残りwait数
			waitEnemy = waitEnemy - wait;
			commentTop1 = commentHero;
			commentTop2 = "wait... " + Integer.toString(waitEnemy);
			//プレイヤーターンが来たのでwait数を０にする
			wait = 0;
			//双方の攻撃ターン
		} else if (wait == waitEnemy) {
			//双方の残りHP
			intHeroHP = intHeroHP - attackEnemy;
			intEnemyHP = intEnemyHP - attack;
			//双方のダメージ量
			atkE = "- " + Integer.toString(attackEnemy);
			atk = "- " + Integer.toString(attack);
			//双方のコメント欄---行動パターンの表示
			commentTop1 = commentHero;
			commentTop2 = commentEnemy;
			//お互いのwait数を０に
			wait = 0;
			waitEnemy = 0;
		}
		/*もし０を下回ったら０にしてバトル終了*/
		if (intHeroHP < 0) {
			intHeroHP = 0;
			battleFlag = 2;
		}
		if (intEnemyHP < 0) {
			intEnemyHP = 0;
			battleFlag = 3;
		}
		//HPの状況を表示する
		heroHP = Integer.toString(intHeroHP);
		enemyHP = Integer.toString(intEnemyHP);
	}

	//行動パターンをぶつけ合うメソッド---未使用
	void eachOtherHit() {
		int i = 0;
		String[] s2 = new String[al1.size()];
		for (String s : al1) {                        //自分
			s2[i] = s;
			i++;
		}
		for (int ii = 0; ii < al1.size(); ii++) {
			if (s2[ii] == e1[ii]) {
				int iii = attack - attackEnemy;
				if (iii > 0) {
				} else if (iii < 0) {
				} else if (iii == 0) {
				}
			}
		}
	}

	//ゲッター
	public ArrayList<String> getAl2() {
		return al2;
	}

	public void setAl2(ArrayList<String> al2) {
		this.al2 = al2;
	}

	//XMLから利用するためのコンストラクタ
	public MagicSquare(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自動生成されたコンストラクター・スタブ
		init(context);
	}

	public MagicSquare(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
		init(context);
	}
}
