import java.io.*;
import java.lang.Math;
public class Simplex
{
   InputStreamReader isr= new InputStreamReader(System.in);
   BufferedReader br=new BufferedReader(isr);
   static int m;
   static int n;
   static double A[][];
   static double b[];
   static double c[];
   static double A1[][];
   static double b1[];
   static double c1[];
   int index_maxcost;
   int soln_index[];
   int minratio_index;
   double p;
   int flag;
   static int n_slack;
   static int m1;
   boolean check_unbounded=false;
   int count_lex;
   int lex[];
   /* The below function is the basic function which takes input of the Linear Programming Problem. 
    * The inputs are as follows: number of decision variables, number of slack variables and number 
    * of constraints. After taking these basic inputs, the matrices A,b,c are defined. A,b,c are used
    * according to the standard representation of LP. 
   */
   void input()throws IOException 
   {
       System.out.println("Enter the number of decision variables");
       m1=Integer.parseInt(br.readLine());
       System.out.println("Enter the number of slack variables");
       n_slack=Integer.parseInt(br.readLine());
       m=m1+n_slack;
       System.out.println("Enter the number of constraints");
       n=Integer.parseInt(br.readLine());
       System.out.println("Enter the cost matrix");
       A=new double[n][m];
       b=new double[n];
       c=new double[m];
       A1=new double[n][m];//copy of A is stored in A1. This is done so that original data is not lost. Similarly done for b and c
       b1=new double[n];
       c1=new double[m];
       for(int i=0;i<m;i++)
       {
           c[i]=Double.parseDouble(br.readLine());
           c1[i]=-c[i];
           /* This is done because here we consider a minimizing linear programming program. We also decide 
            * to start the simplex by using the identity matrix as the basis. If its readily available then
            * we can use it directly if no, steps are taken to obtain an initial identity BFS. Hence, we always 
            * start with the reduced cost coefficients being the negative of the original cost 
            * coefficients for the first iteration.
           */
       }
       System.out.println("Enter the matrix of coefficients");
       for(int i=0;i<n;i++)
       {
           for(int j=0;j<m;j++)
           {
               A[i][j]=Double.parseDouble(br.readLine());
               A1[i][j]=A[i][j];
           }
       }
       System.out.println("Enter the RHS matrix b");
       for(int i=0;i<n;i++)
       {
           b[i]=Double.parseDouble(br.readLine());
           b1[i]=b[i];
       }
   }
   boolean optimalitycheck(double d[])//function to check  zj-cj <= 0, We pass our reduced cost coefficient matrix in this method
   {
       boolean rvalue=false;
       int k=0;
       for(int i=0;i<m;i++)
       {
           if(d[i]>0)
           {
               k=1;
           }
           else
           {
               rvalue=false;
           }
       }
       if(k==1)
       {
           rvalue=true;
       }
       return rvalue;
   }
   boolean checkidentity(double a[][])//check if passed matrix is identity or not
   {
       boolean check=false;
       int c=0;
       for(int i=0;i<a.length;i++)
       {
            for(int j=0;j<a.length;j++)
            {
               if(j==i)
               {
                   if(a[i][j]==1)
                   {
                       check=true;
                       c++;
                   }
               }
               else if(j!=i)
               {
                   if(a[i][j]==0)
                   {
                       check=true;
                       c++;
                   }
               }
            }
       }
       if(c==(a.length*a.length))
       {
           check=true;
       }
       else
       {
           check=false;
       }
       return check;
   }
   void initialbasis()//check if initial basis is identity matrix
   {
       double A2[][];
       A2=new double[n][n];
       for(int i=0;i<A2.length;i++)
       {
           for(int j=0;j<A2.length;j++)
           {
               A2[i][j]=A1[i][j+(m-n)];
               //System.out.print(A2[i][j]+"  ");
           }
           //System.out.println();
       }
       if(checkidentity(A2)==true)//passing A2 which contains the matrix of slack variables
       {
           System.out.println("Initial basis is identity matrix");
       }  
       else
       {
           System.out.println("Initial basis is not an identity matrix");
           flag=1;
       }
   }
   void solutionindexes()//initialising solution indexes which store the variables in the current basis
   {
       soln_index=new int[n];
       for(int i=0;i<n;i++)
       {
           soln_index[i]=(m-n+i);//initialising slack as the initial basis
       }
   }
   int findmaxcost() // to find the maximum value of reduced cost coefficient and determining entering variable
   {
       double maxcost=c1[0];
       int index_maxcost1=0;
       for(int i=0;i<m;i++)
       {
           if(c1[i]>maxcost)
           {
               maxcost=c1[i];
               index_maxcost1=i;
           }
       }
       return index_maxcost1;
   }
   int findminratio()//finding minimum ratio for deciding exiting variable
   {
       index_maxcost=findmaxcost_cycling();//function to take the smallest subscript of positive reduced cost coefficient (Bland's rule)
       int minratio_index1=0;
       double ratio[]=new double[n];
       int counter=0;
       for(int i=0;i<n;i++)
       {
           if(A1[i][index_maxcost]>0)
           {
               ratio[i]=b1[i]/A1[i][index_maxcost];
               //System.out.println(ratio[i]);
           }
           else
           {
               counter++;
               ratio[i]=1000000000;//storing penalty value so that this ratio is not chosen. 
               //This is done because it cannot be left empty.
           }
       }
       /* Above we check if all y(ij) values are positive. If no we increase the counter. 
        * If all of them are negative then the problem is unbounded.
       */
       if(counter==n)//condition for unboundedness.
       {
           System.out.println("The problem is unbounded");
           check_unbounded=true;
       }
       else
       {
          double min_ratio=ratio[0];
          for(int i=0;i<n;i++)
          {
              if(ratio[i]<min_ratio)
              {
                  min_ratio=ratio[i];
                  minratio_index1=i;
              }
          }
          count_lex=0;
          /*The below snippet is used to see if there are ties in the minimum ratio. If yes 
           * then we choose the one with the smallest subscript (Bland's rule) else we choose the minimum ratio in case of no tie.
           */
          for(int i=0;i<n;i++)
          {
              if(min_ratio==ratio[i])
              {
                  count_lex++; // counter to check for ties or duplicates
              }
          }
          /* Below we resolve the issue of choosing the minimum subscript in the following manner: 
           * 1. We first check for duplicates
           * 2. If yes then we stores those values as well as their indexes
           * 3. We then search this array to find the minimum subscript
          */
          if(count_lex>1)
          {
              counter=0;
              int k1[]=new int[count_lex];
              int k2[]=new int[count_lex];
              int count1=0;
              for(int i=0;i<n;i++)
              {
                   if(min_ratio==ratio[i])
                   {
                       k1[count1]=soln_index[i];
                       k2[count1]=i;
                       count1++;
                   }
              }
              int minratio_cycle=k1[0];
              for(int i=0;i<count_lex;i++)
              {
                   if(k1[i]<minratio_cycle)
                   {
                       minratio_cycle=k1[i];
                       minratio_index1=k2[i];
                   }
              }
           }
        }
        return minratio_index1;
   }
   int findmaxcost_cycling()//first instance of positive reduced cost coefficient
   {
       int maxcost_indexcycling=0;
       //int k=0;
       for(int i=0;i<m;i++)
       {
           if(c1[i]>0)
           {
               maxcost_indexcycling=i;
               break;
           }
       }
       return maxcost_indexcycling;
   }                             
   double pivot(int a,int b)//function which returns pivot element from an array
   {
       double pivot_element=0;
       pivot_element=A1[a][b];
       return pivot_element;
   }
   /* Below we perform the following row transformations: 
    * 1. Divide entire row by pivot element to make pivot element equal to 1
    * 2. Using this row make the corresponding columns of the pivot element 
    * equal to zero (except for the pivot element)
    * 3. Repeat the same operation on the RHS matrix b1 
    * 4. Make the reduced cost coefficient corresponding to the entering variable of the basis 0. 
    * Make the reduced cost coefficient corresponding to the index_maxcost index equal to zero.
    * 5. Change the other reduced cost coefficients (non basic variables)
    */
   void rowtransformations()
   {
       double k=0;
       for(int i=0;i<m;i++)
       {
           if(p<0 && A1[minratio_index][i]==0)
           {
               A1[minratio_index][i]=-(A1[minratio_index][i]/p);// necessary as 0/(-ve number)is shown as -0.0 in java here
           }
           else
           {
               A1[minratio_index][i]=A1[minratio_index][i]/p;//dividing entire row by pivot
           }
       }       
       b1[minratio_index]=b1[minratio_index]/p;
       for(int i=0;i<n;i++)
       {
           k=A1[i][index_maxcost];
           for(int j=0;j<m;j++)
           {
               if(i!=minratio_index)
               {
                   A1[i][j]=A1[i][j]-(k*(A1[minratio_index][j]));//operation to make corresponding column elements 0
               }
           }
           if(i!=minratio_index)
           {
               b1[i]=b1[i]-(k*(b1[minratio_index]));//similar operation for RHS matrix b1
           }
       }
       k=c1[index_maxcost];
       for(int i=0;i<m;i++)//changing the reduced cost coefficients
       {
           //k=c1[index_maxcost];
           c1[i]=c1[i]-(k*A1[minratio_index][i]);
       }  
   }
   void displayresults()//function to display final results 
   {
       double soln_matrix[]=new double[m];
       int solution_index[]=new int[m];
       int temp=0;
       for(int i=0;i<m;i++)
       {
           solution_index[i]=i;
           for(int j=0;j<n;j++)
           {
               if(i!=soln_index[j])
               {
                   soln_matrix[i]=0;
               }
               else
               {
                   temp=(int)(Math.round(b1[j]*100));
                   b1[j]=temp/100.0;
                   soln_matrix[soln_index[j]]=b1[j];
                   break;
               }
           }
       }
       System.out.println("The solution is:");
       System.out.println("Decision variables:");
       for(int i=0;i<m1;i++)
       {
           System.out.println("X"+(solution_index[i]+1)+" = " +soln_matrix[i]);
       }
       System.out.println();
       System.out.println("Slack variables:");
       for(int i=m1;i<m;i++)
       {
           System.out.println("X"+(solution_index[i]+1)+" = " +soln_matrix[i]);
       }
       double z=0;
       System.out.println();
       for(int i=0;i<m;i++)
       {
           z=z+(soln_matrix[i]*c[i]);
       }
       System.out.println("Optimal Objective function value: "+z);
   }
   public boolean checkdualfeasibility()//function to check if the given problem is dual feasible
   {
        boolean check_dual=true;
        for(int i=0;i<m;i++)
        {
            if(c1[i]<=0)
            {
                continue;
            }
            else
            {
                check_dual=false;
                break;
            }
        }
        int k=0;
        for(int i=0;i<n;i++)
        {
            if(b1[i]>0)
            {
                k++;
            }
        }
        if(k==n)
        {
            check_dual=false;
        }
        return check_dual;
   } 
   boolean check_solution(double m[])//function to check if matrix is positive. 
   {
       boolean check_solution=true;
       for(int i=0;i<m.length;i++)
       {
           if(m[i]<0)
           {
               check_solution=false;
               break;
           }
       }
       return check_solution;
   }
}

              
   
           
       
       
   
       
   
        
       
       