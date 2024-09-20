
import mpi.*;

public class Main{

    public static void main(String[] args) throws MPIException, InterruptedException {
        
        MPI.Init(args);
        int  rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int[] message = new int[1];

        //System.out.println("Hi from <"+rank+">");

        if(rank%2 == 0){
            if((rank +1)!=size){
                MPI.COMM_WORLD.Send(message, 0, 1, MPI.INT, rank + 1, 0) ;
                System.out.println("Hi from <"+rank+">");
            }
        }
        else{
            if(rank!=0){
                int[] recvBuffer = new int[1];
                MPI.COMM_WORLD.Recv(recvBuffer, 0, 1, MPI.INT, rank - 1, 0);
                message = recvBuffer;
                System.out.println("recieved <"+rank+">");
            }
        }

        MPI.Finalize();
        //System.out.println("Sum of all ranks: " + s);
    }
}
