
//широков
import mpi.*;
import java.time.Duration;
import java.time.Instant;

public class Main {

    private static void initVectors(int[] A, int[] B, int N) {
        for (int i = 0; i < N; i++) {
            A[i] = (int) (Math.random() * 100);
            B[i] = (int) (Math.random() * 100);
        }
    }

    private static int LocalScalar(int[] A, int[] B, int N) {
        int result = 0;
        for (int i = 0; i < N; i++) {
            result += A[i] * B[i];
        }
        return result;
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        Instant startTime = Instant.now();


        int N = 1000000;

        int[] A = new int[N];
        int[] B = new int[N];

        if (rank == 0) {
            initVectors(A, B, N);
        }

        int localN = N / size;
        int[] localA = new int[localN];
        int[] localB = new int[localN];

        //распределения данных из корневого процесса на все процессы в коммуникаторе
        MPI.COMM_WORLD.Scatter(A, 0, localN, MPI.INT, localA, 0, localN, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(B, 0, localN, MPI.INT, localB, 0, localN, MPI.INT, 0);

        int[] localResult = new int[1];
        for (int i = 0; i < 1; i++)
            localResult[0] = LocalScalar(localA, localB, localN);

        int[] globalResult = new int[1];

        MPI.COMM_WORLD.Reduce(localResult, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);

        Instant endTime = Instant.now();
        if (rank == 0) {
            System.out.println("Vector Result: " + globalResult[0]);
            System.out.println("Time = " + Duration.between(startTime, endTime).toMillis()+ " ms.\nNumber of processes = " + size);
        }

        MPI.Finalize();
    }

}
