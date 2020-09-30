import java.io.*;
public class TwoPhase extends Simplex
{
    int twophase_soln[];
    int n_artificial;
    int flag1;
    double c2[];
    double A2[][];
    int redundant_index;
    int k2;
    boolean redundant;
    public void input_artificial()throws IOException//function to input artificial variables
    {
       System.out.println("Enter number of artificial variables to be included");
       n_artificial=Integer.parseInt(br.readLine());
       m=m1+n_slack+n_artificial;
       A=new double[n][m];
       A1=new double[n][m];
       System.out.println("Enter the matrix of coefficients");
       for(int i=0;i<n;i++)
       {
           for(int j=0;j<m;j++)
           {
               A[i][j]=Double.parseDouble(br.readLine());
               A1[i][j]=A[i][j];
           }
       }
       //double c2[];
       c2=new double[m-n_artificial];
       for(int i=0;i<(m-n_artificial);i++)
       {
           c2[i]=c1[i];
       }
       c1=new double[m];
       for(int i=0;i<m;i++)//initialising the Phase-1 Tableau
       {
           if(i<(m-n_artificial))
           {
               c1[i]=0;
           }
           else
           {
               c1[i]=-1;
           }
       }
    }
    void create_solnmatrix()//creating initial solution matrix with identity as the basis
    {
        int count1=0;
        int count=0;
        //int twophase_soln[];
        twophase_soln=new int[n];
        for(int i=m1;i<m;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(A1[j][i]==1)
                {
                    count1=j;
                    count++;
                }
                else if(A1[j][i]==0)
                {
                    count++;
                }
                else
                {
                    break;
                }
            }
            if(count==n)
            {
                twophase_soln[count1]=i;
                //System.out.println(twophase_soln[count1]);
            }
            count=0;
            count1=0;
        }
        for(int j=0;j<n;j++)
        {
            soln_index[j]=twophase_soln[j];
            //System.out.println(twophase_soln[j]);
        }
    }
    /* Below snippet is for phase-1 implementation. Here we first initialise the phase-1 
     * tableau which is then followed by the simplex implementation. At the end of Phase-1 
     * we want to make the artificial variables zero.
     */
    void phase1_method()
    {
        int row_number=0;
        for(int i=0;i<n;i++)
        {
            if(soln_index[i]>(m-n_artificial-1))
            {
                for(int j=0;j<m;j++)
                {
                    c1[j]=c1[j]+A1[i][j];
                    //System.out.print(c1[j]+"  ");
                }
            }
            //System.out.println();
        }
        int k3=0;
        while(optimalitycheck(c1)==true)//impementation of the simplex after the initial tableau is available
        {
            int m=0;
            m=findminratio();
            minratio_index=m;
            double pivot1=0;
            pivot1=pivot(minratio_index,index_maxcost);
            p=pivot1;
            rowtransformations();
            soln_index[minratio_index]=index_maxcost;
        }
        for(int i=0;i<n;i++)
        {
            if(soln_index[i]>(m-n_artificial-1))//checking if artificial variables are still in the basis
            {
                if(b1[i]==0)//checking if the artificial variable present in the basis is equal to zero
                {
                    redundant=true;//identifying redundancy
                    System.out.println("The system of LP is redundant");
                    redundant_index=i;
                    for(int j=0;j<m;j++)
                    {
                        A1[redundant_index][j]=0;
                    }
                    k2++;
                    for(int l=0;l<n;l++)
                    {
                        for(int j=0;j<m;j++)
                        {
                            A[l][j]=A1[l][j];
                        }
                    }
                    convert_redundant();//function to remove redundant rows
                }
                else
                {
                    k3=1;//artificial variable greater than 0 so the LP is infeasible
                    break;
                }
            }
        }
        if(k3==1)
        {
            System.out.println("Infeasible");
            flag1=1;
        }
    }
    void convert_redundant()
    {
        int counter=0;
        int index=0;
        A1=new double[n-k2][m];
        int soln_index2[]=new int[n];
        for(int i=0;i<n;i++)
        {
            soln_index2[i]=soln_index[i];
        }
        soln_index=new int[n-k2];
        for(int i=0;i<n;i++)
        {
            b[i]=b1[i];
        }
        b1=new double[n-k2];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                if(A[i][j]==0)
                {
                    counter++;
                }
            }
            if(counter!=m)
            {
               for(int j=0;j<m;j++)
               {
                    A1[index][j]=A[i][j];
               }
               soln_index[index]=soln_index2[i];
               b1[index]=b[i];
               //System.out.print("Updated index matrix: "+soln_index[index]+"  ");
               index++;
            }
            counter=0;
        }
        n=n-k2;
    }                          
    void phase2_method()//function to implement phase-2 of two phase approach
    {
        int m_new=m-n_artificial;
        m=m-n_artificial;
        A2=new double[n][m_new];//removing the columns of artificial variables
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m_new;j++)
            {
                A2[i][j]=A1[i][j];
            }
        }
        int k=0;
        double k1=0;
        c1=new double[m_new];
        A1=new double[n][m_new];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m_new;j++)
            {
                A1[i][j]=A2[i][j];
            }
        }
        for(int i=0;i<m;i++)
        {
            c1[i]=c2[i];
        }
        for(int i=0;i<n;i++)//initialising phase-2 tableau
        {
            k=soln_index[i];
            k1=c1[k];
            for(int j=0;j<m_new;j++)
            {
                c1[j]=c1[j]-k1*A1[i][j];//making reduced cost coefficients corresponding to basic variables zero
                //System.out.print(c1[j]+"  ");
            }
            System.out.println();
        }
        while(optimalitycheck(c1)==true)//simplex implementation
        {
            int m=0;
            m=findminratio();
            minratio_index=m;
            double pivot1=0;
            pivot1=pivot(minratio_index,index_maxcost);
            p=pivot1;
            soln_index[minratio_index]=index_maxcost;
            rowtransformations();
        }
    }
    void display_results()
    {
        for(int i=0;i<n;i++)
        {
            System.out.println(soln_index[i]+" "+"="+b1[i]);
        }
    }
    void twophase_method()throws IOException // complete function for entire phase-2 approach
    {
        TwoPhase obj= new TwoPhase();
        obj.input_artificial();
        obj.solutionindexes();//initialising solution indexes
        obj.create_solnmatrix();
        obj.phase1_method();//phase-1 implementation
        if(obj.flag1!=1)//checking the feasibility of LP
        {
            obj.phase2_method();
            if(obj.check_unbounded==false)
            {
                obj.displayresults();
            }
        }
    }
}

    

    
        
                    
        
            
            
        
    

