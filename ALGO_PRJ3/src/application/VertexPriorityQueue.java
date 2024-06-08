package application;
import java.util.ArrayList;
import java.util.Comparator;

public class VertexPriorityQueue {
    private ArrayList<Vertex> heap;

    public VertexPriorityQueue() {
        heap = new ArrayList<>();
    }

    public void add(Vertex vertex) {// الميثود الي بتعمل add 
        heap.add(vertex);// بتضيف الفيرتك في الارري ليست 
        int currentIndex = heap.size() - 1; // بحسبلي السايز تبع القيمة الي اضفتها 
        int parentIndex = (currentIndex - 1) / 2;// هاي بتجبلي البيرنت تبعية القيمة الي اضفتها 

        // اذا قيمة الكرنت المضافة اكبر من قيمة البيرنت بخللي يبدل بيناتهن 
        while (currentIndex > 0 && heap.get(currentIndex).getDistance() < heap.get(parentIndex).getDistance()) {
            Vertex temp = heap.get(currentIndex);
            heap.set(currentIndex, heap.get(parentIndex));
            heap.set(parentIndex, temp);

            currentIndex = parentIndex;
            parentIndex = (currentIndex - 1) / 2;
        }
    }
//هاي الميثود بتعملي ريموف للفيرتكس وبتعملي ريتيرن للفيرتيكس الاقل دستانس
    public Vertex poll() {
        if (isEmpty()) {
            return null;
        }

        Vertex result = heap.get(0);
        Vertex lastItem = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, lastItem);
            heapify(0);
        }

        return result;
    }
    // بتعملي ريستور للهيب بروريتي حسب الاندكس الي انا اعطيتو للميثود 
    private void heapify(int index) {
        int smallest = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < heap.size() && heap.get(leftChild).getDistance() < heap.get(smallest).getDistance()) {
            smallest = leftChild;
        }

        if (rightChild < heap.size() && heap.get(rightChild).getDistance() < heap.get(smallest).getDistance()) {
            smallest = rightChild;
        }

        if (smallest != index) {
            Vertex temp = heap.get(index);
            heap.set(index, heap.get(smallest));
            heap.set(smallest, temp);

            heapify(smallest);
        }
    }
    // بتعملي فحص اذا الليست فاظية او لا 
    public boolean isEmpty() {
        return heap.isEmpty();
    }
   

}
