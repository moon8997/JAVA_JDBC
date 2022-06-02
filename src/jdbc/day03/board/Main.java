package jdbc.day03.board;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		TotalController ctrl = new TotalController();
		Scanner sc = new Scanner(System.in);
		
		ctrl.menu_Start(sc);
		
		sc.close();
		System.out.println("~~~ 프로그램 종료 ~~~");
	}

}
