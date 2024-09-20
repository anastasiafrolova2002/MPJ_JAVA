
import mpi.*;

public class Main{

    public static void main(String[] args) throws MPIException, InterruptedException {
        //Задание 2 - неблок режим
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size=MPI.COMM_WORLD.Size();
        Request []r =new Request[size-1];
        Status []s =new Status[size-1];
        if(myrank == 0) {
            char[] message ="Hello from boss!".toCharArray();
            for (int i=1;i<size;i++)
            {
                r[i-1]=MPI.COMM_WORLD.Isend(message, 0, message.length, MPI.CHAR, i, 1);
                s[i-1]=r[i-1].Wait();
            }
            for (int i=0;i<size-1;i++)
            {
                if (r[i].Is_null())  System.out.println("Isend to " + (i+1) + "complete");
            }
            char[] backmessage = new char[15];
            for (int i=1; i<size; i++)
            {
                MPI.COMM_WORLD.Recv(backmessage, 0, 15, MPI.CHAR, i, 2);
                System.out.println("received: from rank " + i + " " + new String(backmessage));
            }
        }
        else {
            char[] message = new char[20] ;
            MPI.COMM_WORLD.Recv(message, 0, 20, MPI.CHAR, 0, 1) ;
            System.out.println("received by: " + myrank + " from rank 0 " +new String(message)) ;
            char [] backmessage="Hello, master".toCharArray();
            MPI.COMM_WORLD.Send(backmessage, 0, backmessage.length, MPI.CHAR, 0, 2) ;
        }
        MPI.Finalize();
        //System.out.println("Sum of all ranks: " + s);
    }
}
