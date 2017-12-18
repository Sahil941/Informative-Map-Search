package MapSearch;

import java.util.*;

public class Node{
	int hCost = 0;
	int h1Cost = 0;
	int h2Cost = 0;
	int h3Cost = 0;
	int h4Cost = 0;
	int h5Cost = 0;
	
	int fCost = 0;
	int x;
	int y;
	int type; // 0 - normal, 1 - start, 2 - goal, 3 - block, 4 - hard to traverse, 9 - highway
	Node parent;
	
	public Node(int x, int y){
		this.x = x;
		this.y = y;
		this.type = 0;
	}
	
	public void setHTT(){
		this.type = 4;
	}
}