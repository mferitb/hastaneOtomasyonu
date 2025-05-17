public class Heap {
    private Hasta[] heap;
    private int size;

    public Heap(int capacity) {
        heap = new Hasta[capacity];
        size = 0;
    }

    public void insert(Hasta h) {
        if (size >= heap.length) return;

        heap[size] = h;
        heapifyUp(size);
        size++;
    }

    public Hasta extractMax() {
        if (size == 0) return null;

        Hasta max = heap[0];
        heap[0] = heap[--size];
        heapifyDown(0);

        return max;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void heapifyUp(int i) {
        while (i > 0 && heap[i].oncelikPuani > heap[(i - 1) / 2].oncelikPuani) {
            swap(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    private void heapifyDown(int i) {
        int largest = i;
        int left = 2 * i + 1, right = 2 * i + 2;

        if (left < size && heap[left].oncelikPuani > heap[largest].oncelikPuani)
            largest = left;
        if (right < size && heap[right].oncelikPuani > heap[largest].oncelikPuani)
            largest = right;

        if (largest != i) {
            swap(i, largest);
            heapifyDown(largest);
        }
    }

    private void swap(int i, int j) {
        Hasta temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
