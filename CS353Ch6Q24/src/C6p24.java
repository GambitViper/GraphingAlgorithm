/**
 * API for Kleinberg & Tardos Exercise 6.24.
 */
public class C6p24 {
  /**
   *
   * @param registrations An array giving the number of registrations
   * in each of the two parties in a particular district.  This array
   * is required to be rectangular, to have its first dimension even,
   * and to have its second dimension exactly two.  Moreover, the sum
   * {@code registrations[i][0]+registrations[i][1]} must be the same
   * for any valid index {@code i}.
   * @return Whether the districts are susceptible to gerrymandering.
   */
  public static boolean susceptible(int[][] registrations) {
	  int n = registrations.length;
	  int m = ( registrations[0][0] + registrations[0][1] );
	  int partyA = 0, partyB = 0;
	  for( int i = 0; i < registrations.length;  i++) {
		  partyA += registrations[i][0];
		  partyB += registrations[i][1]; 
	  }
	  System.out.println("n = " + n);
	  System.out.println("m = " + m);
	  System.out.println("party 0 total: " + partyA);
	  System.out.println("party 1 total: " + partyB);
	  
	  boolean partyAMander = gerrymander(n, m, partyA, registrations, 0);
	  boolean partyBMander = gerrymander(n, m, partyB, registrations, 1);
	  
	  if(partyAMander || partyBMander) {
		  return true;
	  }
	  return false;
  }
  
//  private static boolean gerrymander(int n, int m, int a, int[][] reg, int party) {
//	  boolean[][][] g = new boolean[n][n/2][a-(n * m)/4-1];
//	  
//	  for(int k = 0; k < n; k++) {
//		  g[k][0][0] = true;
//	  }
//	  
//	  for(int k = 1; k < n; k++) {
//		  for(int l = 1; l < n/2; l++) {
//			  for(int w = (n*m)/4 + 1; w < a-(n * m)/4-1; w++) {
//				  if(k == 1 && l == 1 && w == reg[k][party]) {
//					  g[k][l][w] = true;
//				  }else if(g[k-1][l-1][w-reg[k][party]]) {
//					  g[k][l][w] = true;
//				  }else if(g[k-1][l][w]) {
//					  g[k][l][w] = true;
//				  }else {
//					  g[k][l][w] = false;
//				  }
//			  }
//		  }
//	  }
//	  
//	  for(int s = (n*m)/4 + 1; s < a - (n*m)/4-1; s++) {
//		  if(g[n][n/2][s]) {
//			  return true;
//		  }
//	  }
//	  return false;
//  }

  
  private static boolean gerrymander(int n, int m, int a, int[][] reg, int party) {
	  boolean[][][] gerrymanderA = new boolean[n][n/2][a-(n * m)/4-1];
	  System.out.println("a-(n * m)/4-1 = " + (a-(n * m)/4-1));
	  System.out.println("(n*m)/4 + 1 = " + ((n*m)/4 + 1));
	  for(int s = (n*m)/4 + 1; s < a - (n*m)/4 -1; s++) {
		  if(gerryhelp(n - 1, n/2 - 1, s, gerrymanderA, reg, party)) {
			  return true;
		  }
	  }
	  return false;
  }
  
  private static boolean gerryhelp(int k, int l, int w, boolean[][][] g, int[][] reg, int party) {
	  if(g[k][l][w] != false) return g[k][l][w];
	  if(l == 0 && w == 0) {
		  g[k][l][w] =  true;
		  return true;
	  }
	  if(k == 1 && l == 1) {
		  if(w == reg[k][party]) {
			  g[k][l][w] = true;
		  }else {
			  g[k][l][w] = false;
			  return g[k][l][w];
		  }
	  }
	  g[k][l][w] = gerryhelp(k-1, l, w, g, reg, party);
	  g[k][l][w] = gerryhelp(k-1, l-1, w-reg[k][party], g, reg, party);
	  return g[k][l][w];
  }
  
  public static void main(String[] args){
	  int[][] test1 = new int[][]{
	    { 55, 45},
		{ 43, 57},
		{ 60, 40},
		{ 47, 53}
      };
      System.out.println("test1 susceptibility is: " + susceptible(test1));
  }
}