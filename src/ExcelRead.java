import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class ExcelRead extends Simplex // class designed for somewhat large LP's (decision variables > 20)
{
    public static void main()throws IOException // this function reads values directly so the user does not have to enter all values
    {
        ExcelRead obj= new ExcelRead();
        DualSimplex obj1=new DualSimplex();
        Scanner scanner=new Scanner(new File("C:\\Users\\Anirudh Shankar\\Desktop\\lp_project.txt"));//this class is designed for problem 3
        double [] list = new double[576];
        int k=0;
        while(scanner.hasNextDouble())
        {
            list[k++]=scanner.nextDouble();
        }
        /*
         * The text file lp_project and lp_projectb rext files have matrices A and b stored in them respectively.
         */
        Scanner scanner1=new Scanner(new File("C:\\Users\\Anirudh Shankar\\Desktop\\lp_projectb.txt"));
        double [] list1 = new double[24];
        int k1=0;
        while(scanner1.hasNextDouble())
        {
            list1[k1++]=scanner1.nextDouble();
        }
        int count =0;
        A1=new double[24][48];
        b1=new double[24];
        c1=new double[48];
        for(int i=0;i<24;i++)
        {
            for(int j=0;j<24;j++)
            {
                obj1.A1[i][j]=-list[count];
                count++;
            }
        }
        for(int i=0;i<24;i++)
        {
            for(int j=24;j<48;j++)//adding identity matrix of slack variables.
            {
                if((i+24)==j)
                {
                    obj1.A1[i][j]=1;
                }
                else
                {
                    obj1.A1[i][j]=0;
                }
            }
        }
        count=0;
        for(int i=0;i<24;i++)
        {
            obj1.b1[i]=-list1[i];
        }
        for(int j=0;j<48;j++)
        {
            if(j<24)
            {
                obj1.c1[j]=-1;
            }
            else
            {
               obj1.c1[j]=0;
            }
        }
        obj1.c=new double[48];
        for(int i=0;i<48;i++)
        {
            obj1.c[i]=-obj.c1[i];
        }
        //DualSimplex obj1=new DualSimplex();
        obj.m1=24;
        obj1.m=48;
        obj1.n=24;
        obj1.initialbasis();
        obj1.solutionindexes();
        if(obj1.checkdualfeasibility()==true)//since the problem is dual feasible dual simplex is applied
        {
           obj1.solve();
           obj1.flag2=1;
           //check=1;
        }
    }
}
    
        
