package MapSearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Grid extends Application{
	public double sceneWidth = 1280;
	public double sceneHeight = 760;
	double gridWidth = sceneWidth / 160;
	double gridHeight = sceneHeight / 120;
	
	Node[][] grid = new Node[160][120];
	Node start;
	Node end;
	
	public Grid(){}
	
	static PriorityQueue<Node> open = new PriorityQueue<Node>((n1, n2) -> {
		Node node1 = (Node)n1;
		Node node2 = (Node)n2;
		return node1.fCost < node2.fCost? -1:
				node1.fCost > node2.fCost? 1:0;
	});
	
	static boolean closed[][] = new boolean[160][120];
	static int hMode;
	static int e;
	static int hType;
	
	@Override
	public void start(Stage primaryStage) throws IOException{
		Group root = new Group();
		System.gc();
        long start = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		mapGenerator map = new mapGenerator();
		map.genMap();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Search Type:");
		System.out.println("*** 0: Uniform Cost  ***");
		System.out.println("*** 1: Heuristic A*  ***");
		System.out.println("*** 2: Weighted A*   ***");
		System.out.println("*** 3: Sequential A* ***");
		
		hMode = sc.nextInt();
		if (hMode == 2 || hMode == 3){
			System.out.println("Enter scaler weight:");
			e = sc.nextInt();
		}
		if (hMode != 0 && hMode != 3){
			System.out.println("Enter Heuristic Type");
			System.out.println("*** 1: Manhattan   ***");
			System.out.println("*** 2: Diagonal    ***");
			System.out.println("*** 3: Euclidean   ***");
			System.out.println("*** 4: Euclidean^2 ***");
			System.out.println("*** 5: Y           ***");
			hType = sc.nextInt();
		}
		
		FileReader rd = new FileReader("Input.txt");
		BufferedReader buffread = new BufferedReader(rd);
		
		String line;
		StringTokenizer st;
		String cone;
		String ctwo;
		int sc1;
		int sc2;
		int ec1;
		int ec2;
		
		line = buffread.readLine();
		st = new StringTokenizer(line, ",");
		cone = st.nextToken();
		ctwo = st.nextToken();
		sc1 = Integer.parseInt(cone, 10);
		sc2 = Integer.parseInt(ctwo, 10);
		
		line = buffread.readLine();
		st = new StringTokenizer(line, ",");
		cone = st.nextToken();
		ctwo = st.nextToken();
		ec1 = Integer.parseInt(cone, 10);
		ec2 = Integer.parseInt(ctwo, 10);
		
		Grid g = new Grid();
		
		//this queue is new, used to get the smallest h value
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		
		if(hType == 1){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].hCost = Math.abs(i-ec2)+Math.abs(c-ec1); //Manhattan
				}
			}
		}
		else if(hType == 2){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].hCost = diagonalDist(i, ec2, c, ec1);
				}
			}
		}
		else if(hType == 3){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].hCost = euclideanDist(i, ec2, c, ec1);
				}
			}
		}
		else if(hType == 4){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].hCost = Math.abs(c-ec1)*Math.abs(c-ec1)+(Math.abs(i-ec2)+Math.abs(c-ec1));
				}
			}
		}
		else if(hType == 5){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].hCost = Math.abs(i-ec2);
				}
			}
		}
		
		//this is new, setting all the heuristics and adding them to the queue
		//then set the main cost as the smallest value, and clear the queue
		else if(hMode == 3){
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
					g.grid[c][i].h1Cost = Math.abs(i-ec2)+Math.abs(c-ec1); //Manhattan
					pq.add(g.grid[c][i].h1Cost);
					
					g.grid[c][i].h2Cost = diagonalDist(i, ec2, c, ec1);
					pq.add(g.grid[c][i].h2Cost);
					
					g.grid[c][i].h3Cost = euclideanDist(i, ec2, c, ec1);
					pq.add(g.grid[c][i].h3Cost);
					
					g.grid[c][i].h4Cost = Math.abs(c-ec1)*Math.abs(c-ec1)+(Math.abs(i-ec2)+Math.abs(c-ec1));
					pq.add(g.grid[c][i].h4Cost);
					
					g.grid[c][i].h5Cost = Math.abs(i-ec2);
					pq.add(g.grid[c][i].h5Cost);
					
					g.grid[c][i].hCost = pq.poll();
					pq.clear();
				}
			}
		}
		else{
			for(int i = 0; i < 120; i++){
				for(int c = 0; c < 160; c++){
					g.grid[c][i] = new Node(c, i);
				}
			}
		}
		
		for (int i = 1; i < 9; i++){
			line = buffread.readLine();
			st = new StringTokenizer(line, ",");
			String coordx = st.nextToken();
			String coordy = st.nextToken();
			int xcoord = Integer.parseInt(coordx, 10);
			int ycoord = Integer.parseInt(coordy, 10);
			g.generateHTT(g.grid[xcoord][ycoord]);
		}
		
		g.generateBlocked();
		g.generateHighway();
		buffread.close();
		
		g.start = g.grid[sc1][sc2];
		g.end = g.grid[ec1][ec2];
		g.grid[sc1][sc2].type = 1;
		g.grid[ec1][ec2].type = 2;
		
		long startTime = System.nanoTime();
		g.aStar();
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime);
		System.out.println("");
		System.out.println("The total time for the algorithm to run is: " + totalTime + " nanoseconds.");
		
		System.gc();
		long end = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long totalmem = end - start;
		System.out.println("The total memory usage is: " + (totalmem/(1024)/8) + "KB");
		
		if (closed[ec1][ec2]){
			Node current = g.grid[ec1][ec2];
			System.out.println("end f = " + (g.grid[ec1][ec2].fCost - g.grid[ec1][ec2].hCost));
			System.out.println("end h = " + (g.grid[ec1][ec2].hCost));
			current.type = 5;
			
			while (current.parent!=null){
				if (current.parent.parent == null){
					System.out.println("start f = " + (g.grid[sc1][sc2].fCost + g.grid[sc1][sc2].hCost));
					System.out.println("start h = " + (g.grid[sc1][sc2].hCost));
				}
				
				current.type = 5;
				current = current.parent;
			}
			System.out.println();
		}
		g.grid[sc1][sc2].type = 1;
		g.grid[ec1][ec2].type = 2;
		
		for (int i = 0; i < 120; i++){
			for (int c = 0; c < 160; c++){
				gridNode gNode = new gridNode("", (c * gridWidth), (i * gridHeight), g.grid[c][i].type);
				root.getChildren().add(gNode);
			}
		}
		
		sc.close();
		
		Scene scene = new Scene(root, sceneWidth, sceneHeight);
		
		/*ADD MOUSE CLICK EVENT
		 * 
		 */
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	System.out.println("h cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].hCost);
            	System.out.println("f cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].fCost);
            	System.out.println("h1 cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].h1Cost);
            	System.out.println("h2 cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].h2Cost);
            	System.out.println("h3 cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].h3Cost);
            	System.out.println("h4 cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].h4Cost);
            	System.out.println("h5 cost is :" + g.grid[(int) (mouseEvent.getSceneX()/gridWidth)][(int) (mouseEvent.getSceneY()/gridHeight)].h5Cost);
            }
        });
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static class gridNode extends StackPane{
		public gridNode(String name, double x, double y, int type){
			//This creates a rectangle
			Rectangle node = new Rectangle(8, (760/120));
			node.setStroke(Color.BLACK);
			
			switch(type){
				case 0://If it is a normal node
					node.setFill(Color.WHITE);
					break;
				case 1://If it is the start node
					node.setFill(Color.LIMEGREEN);
					break;
				case 2://If it is the goal node
					node.setFill(Color.ORANGERED);
					break;
				case 3://If it is a blocked node
					node.setFill(Color.BLACK);
					break;
				case 4://If it is a HTT node
					node.setFill(Color.LIGHTGREY);
					break;
				case 5://If it is a path node
					node.setFill(Color.BLUEVIOLET);
					break;
				case 9://If it is a highway node
					node.setFill(Color.DODGERBLUE);
					break;
				default:
					break;
			}
			
			Label nodeName = new Label(name);
			setTranslateX(x);
			setTranslateY(y);
			getChildren().addAll(node, nodeName);
		}
	}
	
	public static void main(String[] args) throws IOException{
		launch(args);
	}
	
	public static int euclideanDist(int x1, int x2, int y1, int y2){
		int x = (int) Math.pow(x1 - x2, 2);
		int y = (int) Math.pow(y1 - y2, 2);
		
		return (int)Math.sqrt(x + y);
	}
	
	public static int diagonalDist(int x1, int x2, int y1, int y2){
		int dmax = Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
		int dmin = Math.min(Math.abs(x1 - x2), Math.abs(y1 - y2));
		
		return 14 + 10*(dmax - dmin);
	}
	
	public void generateHTT(Node curr){
		int xOrgin = curr.x - 15;
		int yOrgin = curr.y - 15;
		Random ran = new Random();
		int httCount = 0;
		
		for(int i = xOrgin; i < (xOrgin + 31); i++){
			for(int c = yOrgin; c < (xOrgin + 31); c++){
				if((i >= 0 && i < 160) && (c >= 0 && c < 120)){
					if(ran.nextInt(2) > 0 && httCount < 480){
						this.grid[i][c].setHTT();
						httCount++;
					}
				}
			}
		}
	}
	
	public void generateBlocked(){
		Random r = new Random();
		
		for(int i = 0; i < 120; i++){
			for(int c = 0; c < 160; c++){
				if(r.nextInt(5) == 1){
					this.grid[c][i].type = 3;
				}
			}
		}
	}
	
	public void generateHighway(){
		for(int i = 1; i < 3; i++){
			Random num = new Random();
			int xvalue = num.nextInt(160);
			if(i == 1){
				int curr = 1;
				this.grid[xvalue][0].type = 9;
				
				while(curr <= 119 || xvalue <= 159){
					Random twenty = new Random();
					int chance = twenty.nextInt(100);
					//Stays in the same row
					if(chance <= 20){
						Random d = new Random();
						int direction = d.nextInt(2);
						//Move Right
						if(direction == 1){
							//If original path was from top to bottom
							if(this.grid[xvalue][curr - 1].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								xvalue += 1;
							}
							//If original path is from left to right
							else if(this.grid[xvalue - 1][curr].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								curr += 1;
							}
							//if original path is from right to left
							else if(this.grid[xvalue + 1][curr].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								curr -=1;
							}
						}else if(direction == 2){
							//If original path was from top to bottom
							if(this.grid[xvalue][curr - 1].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								xvalue -= 1;
							}
							//If orginal path is from left to right
							else if(this.grid[xvalue - 1][curr].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								curr -= 1;
							}
							//if original path is from right to left
							else if(this.grid[xvalue + 1][curr].type == 9){
								this.grid[xvalue][curr].type = 9;
								if(xvalue == 159 || curr == 119){
									break;
								}
								curr +=1;
							}
						}
					}
					//Continues to move the same path
					else{
						if(this.grid[xvalue][curr - 1].type == 9){
							this.grid[xvalue][curr].type = 9;
							//System.out.println("Goes to 1st");
							//NEED THIS TO STOP THE ARRAY OUT OF INDEX ERROR
							if(xvalue == 159 || curr == 119){
								break;
							}
							curr += 1;
						}
						if(this.grid[xvalue - 1][curr].type == 9 ){
							this.grid[xvalue][curr].type = 9;
							//System.out.println("Goes to 2nd");
							if(xvalue == 159 || curr == 119){
								break;
							}
							xvalue += 1;
						}
						if(xvalue + 1 <= 159){
							if(this.grid[xvalue + 1][curr].type == 9){
								this.grid[curr][xvalue].type = 9;
								//System.out.println("Goes to 3nd");
								if(xvalue == 159 || curr == 119){
									break;
								}
								xvalue -= 1;
							}
						}else{
							break;
						}
					}
				}
			}
			 else if(i == 2){
			        int curr = 118;
			        this.grid[xvalue][119].type = 9;
			        while(curr <= 0 || xvalue <= 159){
			          Random twenty = new Random();
			          int chance = twenty.nextInt(100);
			          //Stays in the same row
			          if(chance <= 20){
			            Random d = new Random();
			            int direction = d.nextInt(2);
			            //Move Right
			            if(direction == 1){
			              //If original path was from bottom to top
			              if(this.grid[xvalue][curr + 1].type == 9){
			            	  boolean check = false;
			                  if ((xvalue + 1) > 159){
			                    check = true;
			                  }

			                  if (this.grid[xvalue][curr].type != 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println("1");
			                    if(xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    xvalue += 1;
			                  }
			                  else if (check == false){
			                    if (this.grid[xvalue + 1][curr].type != 9){
			                      this.grid[xvalue + 1][curr].type = 9;
			                      if((xvalue + 2) == 159 || curr == 0){
			                        break;
			                      }
			                      xvalue += 2;
			                    }
			                  }
			                  else if (check == true){
			                    break;
			                  }
			                }
			                //If orginal path is from left to right
			                else if(this.grid[xvalue - 1][curr].type == 9){
			                  boolean check = false;
			                  if ((xvalue + 1) > 159){
			                    check = true;
			                  }

			                  if (this.grid[xvalue][curr].type != 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println("2");
			                    if (xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    xvalue += 1;
			                  }
			                  else if (check == false){
			                    if (this.grid[xvalue + 1][curr].type != 9){
			                      this.grid[xvalue + 1][curr].type = 9;
			                      if ((xvalue + 2) == 159 || curr == 0){
			                        break;
			                      }
			                      xvalue += 2;
			                    }
			                  }
			                  else if (check == true){
			                    break;
			                  }
			                }
			                //if original path is from right to left
			                else if(this.grid[xvalue + 1][curr].type == 9){
			                  if ((xvalue + 1) > 159){
			                  }

			                  if (this.grid[xvalue][curr].type != 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println("3");
			                    if (xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    xvalue += 1;
			                  }
			                }
			              }else if(direction == 2){
			                //If original path was from bottom to top
			                if(this.grid[xvalue][curr + 1].type == 9){
			                  boolean check = false;
			                  if ((xvalue - 1) > 159){
			                    check = true;
			                  }

			                  if (this.grid[xvalue][curr].type == 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println("4");
			                    if (xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    xvalue -= 1;
			                  }
			                  else if (check == false){
			                    if (this.grid[xvalue - 1][curr].type != 9){
			                      this.grid[xvalue - 1][curr].type = 9;
			                      if ((xvalue - 2) > 159){
			                        break;
			                      }
			                      xvalue -= 2;
			                    }
			                  }
			                  else if (check == true){
			                    break;
			                  }
			                }
			                //If original path is from left to right
			                else if(this.grid[xvalue - 1][curr].type == 9){
			                  boolean check = false;
			                  if ((xvalue - 1) > 159){
			                    check = true;
			                  }if (this.grid[xvalue][curr].type != 9){
			                      this.grid[xvalue][curr].type = 9;
			                      //System.out.println("5");
			                      if(xvalue == 159 || curr == 0){
			                        break;
			                      }
			                      xvalue -= 1;
			                    }
			                    else if (check == false){
			                      if (this.grid[xvalue - 1][curr].type != 9){
			                        this.grid[xvalue - 1][curr].type = 9;
			                        if ((xvalue - 1) > 159){
			                          break;
			                        }
			                        xvalue -= 2;
			                      }
			                    }
			                    else if (check == true){
			                      break;
			                    }
			                  }
			                  //if original path is from right to left
			                  else if(this.grid[xvalue + 1][curr].type == 9){
			                    boolean check = false;
			                    if ((xvalue - 1) > 159){
			                      check = true;
			                    }

			                    if (this.grid[xvalue][curr].type != 9){
			                      this.grid[xvalue][curr].type = 9;
			                      //System.out.println("6");
			                      if(xvalue == 159 || curr == 0){
			                        break;
			                      }
			                      xvalue +=1;
			                    }
			                    else if (check == false){
			                      this.grid[xvalue - 1][curr].type = 9;
			                      if ((xvalue - 2) > 159){
			                        break;
			                      }
			                      xvalue -= 2;
			                    }
			                    else if (check == true){
			                      break;
			                    }
			                  }
			                }
			              }
			              //Continues to move the same path
			              else{
			                if(this.grid[xvalue][curr + 1].type == 9){
			                  if (this.grid[xvalue][curr].type != 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println("Goes to 1st 2nd loop");
			                    //NEED THIS TO STOP THE ARRAY OUT OF INDEX ERROR
			                    if(xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    curr -= 1;
			                  }
			                }
			                if(this.grid[xvalue - 1][curr].type == 9 ){
			                  if (this.grid[xvalue][curr].type != 9){
			                    this.grid[xvalue][curr].type = 9;
			                    //System.out.println(xvalue);
			                    if(xvalue == 159 || curr == 0){
			                      break;
			                    }
			                    xvalue += 1;
			                  }
			                }
			                if((xvalue + 1) <= 159){
			                  if(this.grid[xvalue + 1][curr].type == 9){
			                    if (this.grid[xvalue][curr].type != 9){
			                      this.grid[xvalue][curr].type = 9;
			                      //System.out.println("Goes to 3nd 2nd loop");
			                      if(xvalue == 159 || curr == 0){
			                        break;
			                      }
			                      xvalue -= 1;
			                    }
			                  }
			                }else{
			                  break;
			                }
			              }
			            }
			          }
			        }  
		
		
		
		for (int j = 0; j < 2; j++){
			Random num = new Random();
			int y = num.nextInt(120);
			for (int i = 0; i < 160; i++){
				this.grid[i][y].type = 9;
			}
		}
	}
	
	public static void updateCost(Node curr, Node temp, int cost){
		if(temp == null || closed[temp.x][temp.y] || temp.type == 3){
			return;
		}
		int totalCost;
		
		if(hMode == 0){
			totalCost = cost; //hCost = 0 for uniform, e*hCost for weighted
		}
		else if(hMode == 1){
			totalCost = temp.hCost + cost;
		}
		else if(hMode == 2){
			totalCost = e*temp.hCost + cost;
		}
		else{
			totalCost = e*(temp.hCost + cost);
		}
		
		boolean exists = open.contains(temp);
		
		if(!exists || (totalCost < temp.fCost)){
			temp.fCost = totalCost;
			temp.parent = curr;
			if(!exists){
				open.add(temp);
			}
		}
	}
	
	
	public void aStar(){
		open.add(this.start);
		Node curr;
		Node temp;
		
		while(true){
			curr = open.poll();
			if(curr == null){
				break;
			}
			closed[curr.x][curr.y] = true;
			
			if(curr.equals(this.end)){
				return;
			}
		
			if(curr.x - 1 >= 0){
				temp = this.grid[curr.x-1][curr.y];
				if(temp.type == 4){
					updateCost(curr, temp, curr.fCost + 20);
				}
				else if(temp.type == 9){
					updateCost(curr, temp, curr.fCost + 5);
				}
				else{
					updateCost(curr, temp, curr.fCost + 10);
				}
				
				if(curr.y-1 >= 0){
					temp = this.grid[curr.x-1][curr.y-1];
					if(temp.type == 4){
						updateCost(curr, temp, curr.fCost + 28);
					}
					else if(temp.type == 9){
						updateCost(curr, temp, curr.fCost + 7);
					}
					else{
						updateCost(curr, temp, curr.fCost + 14);
					}
				}
				if(curr.y+1 < this.grid[0].length){
					temp = this.grid[curr.x-1][curr.y+1];
					if(temp.type == 4){
						updateCost(curr, temp, curr.fCost + 28);
					}
					else if(temp.type == 9){
						updateCost(curr, temp, curr.fCost + 7);
					}
					else{
						updateCost(curr, temp, curr.fCost + 14);
					}
				}
			}
			
			if(curr.y-1 >= 0){
				temp = this.grid[curr.x][curr.y-1];
				if(temp.type == 4){
					updateCost(curr, temp, curr.fCost + 20);
				}
				else if(temp.type == 9){
					updateCost(curr, temp, curr.fCost + 5);
				}
				else{
					updateCost(curr, temp, curr.fCost + 10);
				}
			}
			
			if(curr.y+1 < this.grid[0].length){
				temp = this.grid[curr.x][curr.y+1];
				if(temp.type == 4){
					updateCost(curr, temp, curr.fCost + 20);
				}
				else if(temp.type == 9){
					updateCost(curr, temp, curr.fCost + 5);
				}
				else{
					updateCost(curr, temp, curr.fCost + 10);
				}
			}
			
			if(curr.x+1 < this.grid.length){
				temp = this.grid[curr.x+1][curr.y];
				if(temp.type == 4){
					updateCost(curr, temp, curr.fCost + 20);
				}
				else if(temp.type == 9){
					updateCost(curr, temp, curr.fCost + 5);
				}
				else{
					updateCost(curr, temp, curr.fCost + 10);
				}
				
				if(curr.y-1 >= 0){
					temp = this.grid[curr.x+1][curr.y-1];
					if(temp.type == 4){
						updateCost(curr, temp, curr.fCost + 28);
					}
					else if(temp.type == 9){
						updateCost(curr, temp, curr.fCost + 7);
					}
					else{
						updateCost(curr, temp, curr.fCost + 14);
					}
				}
				
				if(curr.y+1 < this.grid[0].length){
					temp = this.grid[curr.x+1][curr.y+1];
					if(temp.type == 4){
						updateCost(curr, temp, curr.fCost + 28);
					}
					else if(temp.type == 9){
						updateCost(curr, temp, curr.fCost + 7);
					}
					else{
						updateCost(curr, temp, curr.fCost + 14);
					}
				}
			}
		}
	}
}