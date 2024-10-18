
import mpi.*;
import mpi.MPIException;


public class Main{

    public static void main(String[] args) throws MPIException, InterruptedException {
    // 2 - a
        /*
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int TAG = 0;
        int s = 0;

        int buf = myrank;

        int nextRank = (myrank + 1) % size;
        int prevRank = (myrank - 1 + size) % size;

        if (myrank == 0) {
            int[] output = new int[]{0};
            MPI.COMM_WORLD.Sendrecv(new int[]{buf}, 0, 1, MPI.INT, nextRank, TAG,
                    output, 0, 1, MPI.INT, prevRank, TAG);

            System.out.println("Total sum: " + output[0]);
        } else {
            int[] output = new int[]{0};
            MPI.COMM_WORLD.Recv(output, 0, 1, MPI.INT, prevRank, TAG);

            System.out.println("Prev rank: " + prevRank);
            System.out.println("Current rank: " + myrank);
            s += output[0] + myrank;
            MPI.COMM_WORLD.Send(new int[]{s}, 0, 1, MPI.INT, nextRank, TAG);
            System.out.println("Next rank: " + nextRank );
            System.out.println("----------------- "  );
        }

        MPI.Finalize();
        */

        //2 - b непрерывная передача по кольцу
/*
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int TAG = 0;

        int buf = myrank;
        int nextRank = (myrank + 1) % size;
        int prevRank = (myrank - 1 + size) % size;

        int[] output = new int[1];
        Request sendRequest = null;
        Request recvRequest = null;

        if (myrank == 0) {
            // 0 также отправляет данные, он сначала инициирует получение данных

            // отправляем данные
            sendRequest = MPI.COMM_WORLD.Isend(new int[]{buf}, 0, 1, MPI.INT, nextRank, TAG);

            sendRequest.Wait();
            recvRequest = MPI.COMM_WORLD.Irecv(output, 0, 1, MPI.INT, prevRank, TAG);

            // ждем завершения сенд и ресив
            //recvRequest.Wait();

            //System.out.println("Total sum: " + output[0]);
        } else {
            // Все остальные процессы сначала принимают данные
            recvRequest = MPI.COMM_WORLD.Irecv(output, 0, 1, MPI.INT, prevRank, TAG);

            // ждем завершения получения
            recvRequest.Wait();

            int s = output[0] + myrank;
            System.out.println("Previous rank: " + prevRank);
            System.out.println("Current rank: " + myrank);
            System.out.println("Next rank: " + nextRank);
            System.out.println("Current sum: " + s);
            System.out.println("-------------------- ");

            // отправляем данные следующему процессу
            sendRequest = MPI.COMM_WORLD.Isend(new int[]{s}, 0, 1, MPI.INT, nextRank, TAG);

            // ждем завершения отправки
            sendRequest.Wait();
            //System.out.println("Total sum: " + output[0]);
        }

*/


        //лр3

        MPI.Init(args);
        int data[] = new int[1];
        int buf[] = {5,3,1, 7, 9, 8};
        int back_buf[] = new int[10];
        int back_buf2[] = new int[10];
        int count, TAG = 0, exch;
        boolean swapped;
        Status st;

        data[0] = 2016;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            MPI.COMM_WORLD.Send(data, 0, 1, MPI.INT, 2, TAG);
        } else if (rank == 1) {
            MPI.COMM_WORLD.Isend(buf, 0, buf.length, MPI.INT, 2, TAG);
        } else if (rank == 2) {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            MPI.COMM_WORLD.Recv(back_buf, 0, count, MPI.INT, 0, TAG);
            System.out.print("Rank = 0 \n");
            for (int i = 0; i < count; i++)
                System.out.print(back_buf[i] + " ");

            st = MPI.COMM_WORLD.Iprobe(1, TAG);
            count = st.Get_count(MPI.INT);
            MPI.COMM_WORLD.Irecv(back_buf2, 0, count, MPI.INT, 1, TAG);

            System.out.print("\nRank = 1 \n");
            for (int i = 0; i < count; i++)
                System.out.print(back_buf2[i] + " ");
            for (int i = 0; i < count - 1; i++) {
                swapped = false;
                for (int j = 0; j < count - i - 1; j++) {
                    if (back_buf2[j] > back_buf2[j + 1]) {
                        exch =back_buf2[j];
                        back_buf2[j] = back_buf2[j+1];
                        back_buf2[j+1] = exch;
                        swapped = true;
                    }
                }

                // If no two elements were swapped, then break
                if (!swapped)
                    break;
            }
            System.out.print("\nsorted\n");
            for (int i = 0; i < count; i++)
                System.out.print(back_buf2[i] + " ");
        }
        MPI.Finalize();

    }
}
