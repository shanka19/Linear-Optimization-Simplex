import java.io.*;
/*
 * This class is designed to make the algorithm easier to view and debug.
 * It helps in understanding the components and their working. A small compact class for the
 * entire algorithm which calls the required methods. 
 */
public class Solver extends Simplex//common class to start the entire algorithm
{
   public static void main()throws IOException
   {
       InputStreamReader isr= new InputStreamReader(System.in);
       BufferedReader br=new BufferedReader(isr);
       int check=0;
       Simplex obj=new Simplex();
       DualSimplex obj1=new DualSimplex();
       obj.input();
       obj.initialbasis();
       obj.solutionindexes();       
       TwoPhase obj2=new TwoPhase();
       if(obj.flag==1)
       {
           obj2.twophase_method();
           check=1;
       }
       if(obj.checkdualfeasibility()==true)
       {
           obj1.solve();
           obj1.flag2=1;
           check=1;
       }
       while(obj.optimalitycheck(c1)==true && obj.flag==0 && obj.check_solution(b1)==true)
       {
           obj.minratio_index=obj.findminratio();
           if(obj.check_unbounded==false)
           {
                obj.p=obj.pivot(obj.minratio_index,obj.index_maxcost);
                obj.soln_index[obj.minratio_index]=obj.index_maxcost;
                obj.rowtransformations();
           }
           else
           {
                break;
           }
       }
       if(obj.check_unbounded==false && check!=1 && obj.check_solution(b1)==true)
       {
            obj.displayresults();
       }
       if(obj.check_solution(b1)==false)
       {
           System.out.println("The LP is infeasible, Change values in RHS matrix");
       }
       int c;
       System.out.println("Do you wish to continue? Press 1 to continue and 0 to end");
       c=Integer.parseInt(br.readLine());
       while(c==1)
       {
           main();
       }
    }
}
    
       


