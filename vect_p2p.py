

///////// point to point

import mpi.*;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        Instant startTime = Instant.now();

        int N = 1000000;
        int localN = N / size; // Размер локальной части для каждого процесса

        int[] A = new int[N];
        int[] B = new int[N];
        int[] localA = new int[localN];
        int[] localB = new int[localN];

        // Инициализация векторов только в процессе с rank 0
        if (rank == 0) {
            initializeVectors(A, B, N);
        }

        // Двухточечная передача векторов A и B
        // Процесс 0 отправляет свои данные
        if (rank == 0) {
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(A, i * localN, localN, MPI.INT, i, 0);
                MPI.COMM_WORLD.Send(B, i * localN, localN, MPI.INT, i, 1);
            }
            // Процесс 0 получает свою локальную часть
            System.arraycopy(A, 0, localA, 0, localN);
            System.arraycopy(B, 0, localB, 0, localN);
        } else {
            // Все остальные процессы получают свои данные
            MPI.COMM_WORLD.Recv(localA, 0, localN, MPI.INT, 0, 0);
            MPI.COMM_WORLD.Recv(localB, 0, localN, MPI.INT, 0, 1);
        }

        // Вычисляем локальное скалярное произведение
        int localResult = calcLocalScalar(localA, localB, localN);

        // Процесс 0 собирает результаты от всех процессов
        int globalResult = 0;
        if (rank == 0) {
            globalResult = localResult; // Начинаем с локального результата процесса 0
            for (int i = 1; i < size; i++) {
                int receivedResult = 0; // Переменная для получения результатов от других процессов
                MPI.COMM_WORLD.Recv(new int[]{receivedResult}, 0, 1, MPI.INT, i, 2);
                globalResult += receivedResult; // Суммируем результаты
            }
        } else {
            // Все остальные процессы отправляют свои локальные результаты
            MPI.COMM_WORLD.Send(new int[]{localResult}, 0, 1, MPI.INT, 0, 2);
        }

        Instant endTime = Instant.now();

        if (rank == 0) {
            System.out.println("Result: " + globalResult);
            System.out.println("The program finished its work in " + Duration.between(startTime, endTime).toMillis() + " ms. and the number of processes " + size);
        }

        MPI.Finalize();
    }

    private static void initializeVectors(int[] A, int[] B, int N) {
        for (int i = 0; i < N; i++) {
            A[i] = (int) (Math.random() * 100);
            B[i] = (int) (Math.random() * 100);
        }
    }

    private static int calcLocalScalar(int[] A, int[] B, int N) {
        int result = 0;
        for (int i = 0; i < N; i++) {
            result += A[i] * B[i];
        }
        return result;
    }
}
