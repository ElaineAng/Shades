package game.Assignment2_ElaineAng;
//Name: Elaine Ang 
//NetID: ra1695
//Assignment 2 Shades Game
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game2 extends JFrame{
	public static void main(String[] args){
		new Game2();		
	}

	public JLabel scoreGrade=new JLabel("0");//Make this label public so that it can refresh everytime.
	
	//Main class.
	public Game2(){	
		setTitle("Shades");
		setSize(500,570);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		Board game=new Board();
		Status status=new Status();
		new Thread(game).start();
		add(game,BorderLayout.WEST);
		add(status,BorderLayout.EAST);
		
		game.setVisible(true);
		status.setVisible(true);
		setVisible(true);
	}
	/*
	 * Status is the panel for displaying scores.
	 * It contains two JLabel, one displaying the String "Score";
	 * The other one displaying the actual score.
	 */
	public class Status extends JPanel{ 
		public JLabel scoreTitle=new JLabel("Score    ",JLabel.CENTER);
		public Status(){
			this.setSize(150,570);
			this.setLayout(new GridLayout(8,1));

			scoreTitle.setFont(new Font("Serif",Font.BOLD,20));
			scoreGrade.setFont(new Font("Serif",Font.BOLD,20));	
			this.add(scoreTitle);
			this.add(scoreGrade);
			
		}

	}
	/*
	 * Block is a class contains a JLabel that is the rectangle.
	 * 
	 */
	public class Block {
		public int _x,_y,_level,_visible;
		public boolean _reset,_movable;
		public JLabel block=new JLabel();
		
		public Block(){
			_visible=0;
			_reset=true;
			_movable=true;
		}
		
		public void setVisible(){
			_visible=1;
		}
		
		//Note that in order to make resetBlock works,
		//we need to do enableReset after we reset a new block.
		//We cannot 
		public void resetBlock(int level,int x,int y){
				if (_reset){
					_reset=false;
					_x=x;
					_y=y;
					_level=level;
					color(_level);
					block.setBounds(100*_x,_y,100,50);
				}
		}
		public void repaint(int level){
			_level=level;
			color(_level);
		}
		public void enableReset(){
			_reset=true;
		}
		public void falling(int y){
			_y=y;
			block.setBounds(100*_x,_y,100,50);
		}
		//BolckMove and enableMove works to ensure that the block moves properly during pause.
		//They also ensure that one block will not go through another horizontally.
		public void blockMove(){  
			_movable=false;
		}
		public void enableMove(){
			_movable=true;
		}
		public void moveleft(int count){
			if ((count<=_x) && (_y<500) && _movable){
				_x-=1;
				block.setBounds(100*_x,_y,100,50);
			}		
		}
		public void moveright(int count){
			if (count<=3-_x && _y<500 && _movable){
				_x+=1;
				block.setBounds(100*_x,_y,100,50);			
			}
		}
		public void movedown(Block a,int prev[],boolean same,int levelcount){
			if (_movable){
				if (same){
					_y=500-(prev[a.getx()]-levelcount)*50;
				}
				else{
					_y=500-(prev[a.getx()]*50);
				}
				block.setBounds(100*_x,_y,100,50);
			}

		}
		public void color(int level){  //5 levels of colors
			if (level==0){
				block.setBackground(Color.white);
			}
			if (level==1){
				block.setBackground(Color.lightGray);
			}
			if (level==2){
				block.setBackground(Color.gray);
			}
			if (level==3){
				block.setBackground(Color.darkGray);
			}
			if (level==4){
				block.setBackground(Color.black);
			}
		}
		public void show(){
			block.setOpaque(true);
			add(block);
		}

		public void disappear(){
			block.setBounds(-5,-100,0,0);
		}
		public int getx(){
			return _x;
		}
		public int gety(){
			return _y;
		}
		public int getlevel(){
			return _level;
		}
	}
	
	
	/*
	 * Board is a class that runs the game.
	 * The game contains two parts.
	 * A single JLabel, a, keeps falling down until it touches the bottom.
	 * All the block that was piled up was stored in a 2-D 11*4 array.
	 * Whenever the JLabel a touches the its bottom, a corresponding new block in that array was created and showed.
	 */
	public class Board extends JPanel implements KeyListener,Runnable{
		public Board(){
			setVisible(true);
			setSize(400,570);
			setLayout(null);		
		}
	
		public Block a=new Block(),BA[][]= new Block[4][11];//BA refers to BlockArray.
		public int prev[]=new int[4],lc,score=0; 
		public boolean pause=false,same,running=true;
		public void run(){			
			boolean touchBottom=false;
			int ci,cj,count=-50;

			Random rand=new Random();
			addKeyListener(this);
			
			while (running){
				same=false;  
				this.requestFocus();
				
				/* This part enables one block, a, to fall from top to its bottom.
				 * When a reaches its bottom, it was put to the top again and fall again.
				 */
				int l=rand.nextInt(4)+0; //The darkest block never falls.
				int x=rand.nextInt(4)+0;
				a.resetBlock(l,x,count); 
				
				if (prev[a.getx()]>0){  //This if-else statement deals with having merge case or not.
					Block buff=BA[a.getx()][11-prev[a.getx()]];
					if (buff!=null){
						int ls=buff.getlevel();
						if (a.getlevel()==ls && ls<4){
							same=true;
							lc=1; //level count
							if (prev[a.getx()]>1){  //This if-else statement deals with having one merging or multiple merging.
								for (int jc=11-prev[a.getx()];jc<10;jc++){
									if (BA[a.getx()][jc]!=null){
										if (1+BA[a.getx()][jc].getlevel()==BA[a.getx()][jc+1].getlevel()){
											lc+=1;
										}
									}
								}
							}
							if(a.gety()!=500-(prev[a.getx()]-lc)*50){  //does not touch the bottom, keep falling.
								a.falling(count);
								
							}
							if (a.gety()==500-(prev[a.getx()]-lc)*50){  //touch its bottom, stop falling.
								count=-50;
								touchBottom=true;
								a.enableReset();
							}
						}
						else{ 
							if(a.gety()!=500-prev[a.getx()]*50){
								a.falling(count);
							}
							if (a.gety()==500-prev[a.getx()]*50){
								count=-50;
								touchBottom=true;
								a.enableReset();
							}
						}
					}					
				}
				else{  //Does not have merge case.
					if(a.gety()!=500-prev[a.getx()]*50){
						a.falling(count);
					}
					if (a.gety()==500-prev[a.getx()]*50){
						count=-50;
						touchBottom=true;
						a.enableReset();
					}
				}
				count+=1;
				
				try{
					Thread.sleep(10);						
				}
				catch(Exception e){
					e.printStackTrace();
				}

				/*
				 * This part deals with pause.
				 */				
				if (pause){
					synchronized(this){
						while(pause){
							try {
								a.blockMove();
								wait();
							} 
							catch (InterruptedException e) {
								a.enableMove();
							}
						}					
					}
				}				
				/*
				 * This part deals with how to show the 4*11 array of blocks after block a touches its bottom.
				 */
				if (touchBottom){	
					touchBottom=false;
					int x0=a.getx();
					int y0=a.gety();
					int l0=a.getlevel();
					// [ci][cj]is the index where we show our corresponding blocks. 
					ci=x0;
					cj=y0/50;

					if(cj>=0){
						while (BA[ci][cj]!=null){			
							cj-=1;
						}
					}
					
					if (cj>=0){ 
						//This if controls the merge of blocks.
						if (cj<10 && l0<4 && l0==BA[ci][cj+1].getlevel()){  
							BA[ci][cj+1].repaint(l0+1);
							score+=10;
							for (int j=cj+1;j<10;j++){
								int l1=BA[ci][j].getlevel(),l2=BA[ci][j+1].getlevel();
								if (l1<4 && l1==l2){
									score+=10;
									BA[ci][j].disappear();
									BA[ci][j+1].repaint(l1+1);
									prev[ci]-=1;
									BA[ci][j]=null;	
									
									display();
								}
								else{
									break;
								}	
							}	
						}	
						//This deals with the situation where no merge happens.
						else{  
							BA[ci][cj]=new Block();
							BA[ci][cj].resetBlock(l0,x0,y0);
							prev[ci]+=1;
							BA[ci][cj].setVisible();
							display();
						}													
					}					
					/*
					 * This part deals with the erasing of a whole line when color becomes identical.
					 */					
					for (int j=10;j>=0;j--){
						if (BA[0][j]!=null && BA[1][j]!=null && BA[2][j]!=null && BA[3][j]!=null){
							
							if (BA[0][j].getlevel()==BA[1][j].getlevel() 
								&& BA[1][j].getlevel()==BA[2][j].getlevel()
								&& BA[2][j].getlevel()==BA[3][j].getlevel()){
								try{
									Thread.sleep(500);
								}
								catch (Exception e){}
								score+=100;
								for (int i=0;i<4;i++){
									prev[i]-=1;
									BA[i][j].disappear();
									BA[i][j]=null;
								} //one line disappears 
								for (int j1=j-1;j1>=0;j1--){
									for (int i=0;i<4;i++){
										if (BA[i][j1]!=null){
											BA[i][j1+1]=new Block();
											BA[i][j1+1].resetBlock(BA[i][j1].getlevel(),BA[i][j1].getx(),BA[i][j1].gety()+50);
											BA[i][j1+1].setVisible();
											BA[i][j1+1].enableReset();
											BA[i][j1].disappear();
											BA[i][j1]=null;
										}
									}
								}//the above lines fall down.
								display();
							}
						}
						else{
							break;
						}
						
					}
				scoreGrade.setText(String.valueOf(score));
				} //end if (touchBottom)
				a.show();
			}//end run while
		}
		public void display(){
			outerloop:
			for (int i=0;i<4;i++){
				for (int j=0;j<11;j++){
					if (BA[i][j]!=null){
						if (BA[i][j]._visible==1){
							BA[i][j].show();
						}
					//System.out.print(i+" "+j+"is filled; ");
					}
					if (BA[i][0]!=null){
						JOptionPane.showMessageDialog(this, "Game Over!", "Shades", JOptionPane.INFORMATION_MESSAGE);
						running=false;
						break outerloop;
					}
				}
			}
			//System.out.println();
		}
		public void keyTyped(KeyEvent e) {}
		public synchronized void keyPressed(KeyEvent e) {
			int key=e.getKeyCode();
			if (key==37){ //The KeyCode for Right key is 37.
				try{
					if (a.getx()>0 && BA[a.getx()-1][a.gety()/50+1]!=null){
						a.blockMove(); //When its left side already has a block.
					}
					else if (!pause){ //All other situations except for pause.
						a.enableMove();
					}
					
				}
				catch (Exception ior){}
				a.moveleft(1);
				
			}
			if (key==39){ //The KeyCode for Left key is 39.
				try{
					if (a.getx()<3 && BA[a.getx()+1][a.gety()/50+1]!=null){
						a.blockMove(); //When its right side already has a block.
					}
					else if (!pause){ //All other situations except for pause.
						a.enableMove();
					}
				}
				catch (Exception ior){}
				a.moveright(1);
			}	
			if (key==40){ //The DownCode for Down key is 40.
				if (!pause){
					a.enableMove();
				}
				a.movedown(a,prev,same,lc);
			}
			if (key==80){ //The KeyCode for "p" is 80.
				pause=!pause;
				if (!pause){
					notify();
				}
			}
		}
		public void keyReleased(KeyEvent e) {}		
	}
}
