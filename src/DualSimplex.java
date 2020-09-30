import java.lang.Math;
public class DualSimplex extends Simplex
{
    int flag2=0;
    boolean check_dualunbounded=false;
    public boolean checkdualfeasibility()//checking if LP is dual feasible
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
            if(b1[i]>=0)
            {
                k++;
            }
        }
        if(k==n)
        {
            check_dual=false;
        }
        flag2=1;
        return check_dual;
    }
    int index_minimum_b(double a[])//function to find minimum value of b (from RHS)
    {
        int minimum_index=0;
        double k=a[0];
        for(int i=0;i<a.length;i++)
        {
            if(a[i]<0)//detecting the first negative b(RHS)
            {
                k=a[i];
                minimum_index=i;
                break;//application of bland's rule to prevent cycling
            }
        }
        return minimum_index;
    }
    void dual_method()
    {
        int k=0;
        int minimum_index=index_minimum_b(b1);
        double min_ratio[];
        min_ratio=new double[m];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                if(A1[minimum_index][j]<0)//calculation of ratios
                {
                    min_ratio[j]=c1[j]/A1[minimum_index][j];
                }
                else
                {
                    min_ratio[j]=100000000;//large penalty as default empty value in array is zero
                    k++;
                }
            }
        }
        if(k==m)//checking dual unboundedness
        {
            System.out.println("The primal is infeasible");
            check_dualunbounded=true;
        }
        else
        {
            
            double a=min_ratio[0];
            int minrcost_index=0;
            for(int i=0;i<m;i++)
            {
                if(min_ratio[i]<a)
                {
                    a=min_ratio[i];
                    minrcost_index=i;
                }
            }
            int count_lex=0;
            for(int i=0;i<m;i++)
            {
                if(a==min_ratio[i])
                {
                    count_lex++; // counter to check for ties or duplicates
                }
            }
            /*
               * The below snippet is similar to the one we see in Simplex class. This function checks for ties 
               * and then applies Bland's rule to break the tie.
             */
            if(count_lex>1)
            {
              int counter=0;
              int k2[]=new int[count_lex];
              int count1=0;
              for(int i=0;i<m;i++)
              {
                   if(a==min_ratio[i])
                   {
                       k2[count1]=i;
                       count1++;
                   }
              }
              int minratio_cycle=k2[0];
              for(int i=0;i<count_lex;i++)
              {
                   if(k2[i]<minratio_cycle)
                   {
                       minratio_cycle=k2[i];
                   }
              }
              minrcost_index=minratio_cycle;
            }
            p=pivot(minimum_index,minrcost_index);
            index_maxcost=minrcost_index;
            minratio_index=minimum_index;
            soln_index[minratio_index]=index_maxcost;
            rowtransformations();//rowtransformations after pivot is obtained
        }
    }
    void solve()
    {
        DualSimplex obj1=new DualSimplex();
        obj1.solutionindexes();
        while(obj1.checkdualfeasibility()==true)//loop to carry out the dual simplex method until optimality is reached
        {
            obj1.dual_method();
        }
        if(check_dualunbounded==false)//displaying results only if solution is obtained
        {
            obj1.displayresults();
        }
    }
}                

            
            
    

        
        
    
