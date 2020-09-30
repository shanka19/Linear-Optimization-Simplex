import java.io.*;
class Main 
{
    public static void main(String args[])
    {
        String str[]=args[0].split(",");
        for(int i=0;i<str.length;i++)
        {
            System.out.print(str[i]);
        }
        for(int i=0;i<str.length;i++)
        {
            for(int j=i+1;j<str.length;j++)
            {
                if(str[i].equals(str[j]))
                {
                    str[j]=" ";
                }
            }
        }
        for(int i=0;i<str.length;i++)
        {
            if(str[i]!=" ")
            {
                System.out.print(str[i]+" ");
            }
        }
    }
}
        
        

            
        