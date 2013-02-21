package com.jwetherell.algorithms.data_structures;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public interface Queue<T> extends IQueue<T> {

    /**
     * This queue implementation is backed by an array.
     * 
     * @author Justin Wetherell <phishman3579@gmail.com>
     */
    public static class ArrayQueue<T> implements Queue<T> {

        private static final int MINIMUM_SIZE = 10;

        private T[] array = (T[]) new Object[MINIMUM_SIZE];
        private int lastIndex = 0;
        private int firstIndex = 0;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean offer(T value) {
            int length = lastIndex - firstIndex;
            if (length >= array.length) {
                array = Arrays.copyOfRange(array, firstIndex, ((lastIndex * 3) / 2) + 1);
                lastIndex = lastIndex - firstIndex;
                firstIndex = 0;
            }
            array[lastIndex++] = value;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T poll() {
            int length = lastIndex - firstIndex;
            if (length < 0)
                return null;

            T t = array[firstIndex];
            array[firstIndex++] = null;

            length = lastIndex - firstIndex;
            if (length <= 0) {
                // Removed last element
                lastIndex = 0;
                firstIndex = 0;
            }

            if (length >= MINIMUM_SIZE && (array.length - length) >= length) {
                array = Arrays.copyOfRange(array, firstIndex, lastIndex);
                lastIndex = length;
                firstIndex = 0;
            }

            return t;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T peek() {
            return array[firstIndex];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(T value) {
            int size = size();
            for (int i = 0; i < size; i++) {
                T obj = array[i];
                if (obj.equals(value)) {
                    return remove(i);
                }
            }
            return false;
        }

        private boolean remove(int index) {
            int size = size();
            if (index<0 || index >=size) return false;

            if (index != --size) {
                // Shift the array down one spot
                System.arraycopy(array, index + 1, array, index, size - index);
            }
            array[size] = null;

            if (size >= MINIMUM_SIZE && size < array.length / 2) {
                array = Arrays.copyOf(array, size);
            }

            lastIndex--;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(T value) {
            for (int i = firstIndex; i < lastIndex; i++) {
                T obj = array[i];
                if (obj.equals(value)) return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean validate() {
            int localSize = 0;
            for (int i=0; i<array.length; i++) {
                T t = array[i];
                if (i<size()) {
                    if (t==null) return false;
                    localSize++;
                } else {
                    if (t!=null) return false;
                }
            }
            return (localSize==size());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return lastIndex - firstIndex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Queue<T> toQueue() {
            return (new JavaCompatibleArrayQueue<T>(this));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Collection<T> toCollection() {
            return (new JavaCompatibleArrayQueue<T>(this));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = lastIndex - 1; i >= firstIndex; i--) {
                builder.append(array[i]).append(", ");
            }
            return builder.toString();
        }
    }

    /**
     * This queue implementation is backed by a linked list.
     * 
     * @author Justin Wetherell <phishman3579@gmail.com>
     */
    public static class LinkedQueue<T> implements Queue<T> {

        private Node<T> head = null;
        private Node<T> tail = null;
        private int size = 0;

        public LinkedQueue() {
            head = null;
            tail = null;
            size = 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean offer(T value) {
            return add(new Node<T>(value));
        }

        /**
         * Enqueue the node in the queue.
         * 
         * @param node
         *            to enqueue.
         */
        private boolean add(Node<T> node) {
            if (head == null) {
                head = node;
                tail = node;
            } else {
                Node<T> oldHead = head;
                head = node;
                node.next = oldHead;
                oldHead.prev = node;
            }
            size++;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T poll() {
            T result = null;
            if (tail != null) {
                result = tail.value;

                Node<T> prev = tail.prev;
                if (prev != null) {
                    prev.next = null;
                    tail = prev;
                } else {
                    head = null;
                    tail = null;
                }
                size--;
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T peek() {
            return (tail!=null)?tail.value:null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(T value) {
            // Find the node
            Node<T> node = head;
            while (node != null && (!node.value.equals(value))) {
                node = node.next;
            }
            if (node == null) return false;
            return remove(node);
        }

        private boolean remove(Node<T> node) {
            // Update the tail, if needed
            if (node.equals(tail))
                tail = node.prev;

            Node<T> prev = node.prev;
            Node<T> next = node.next;
            if (prev != null && next != null) {
                prev.next = next;
                next.prev = prev;
            } else if (prev != null && next == null) {
                prev.next = null;
            } else if (prev == null && next != null) {
                // Node is the head
                next.prev = null;
                head = next;
            } else {
                // prev==null && next==null
                head = null;
            }
            size--;
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(T value) {
            if (head == null)
                return false;

            Node<T> node = head;
            while (node != null) {
                if (node.value.equals(value))
                    return true;
                node = node.next;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean validate() {
            java.util.Set<T> keys = new java.util.HashSet<T>();
            Node<T> node = head;
            if (node!=null) {
                if (node.prev!=null) return false;
                if (node!=null && !validate(node,keys)) return false;
            }
            return (keys.size()==size());
        }

        private boolean validate(Node<T> node, java.util.Set<T> keys) {
            if (node.value==null) return false;
            keys.add(node.value);

            Node<T> child = node.next;
            if (child!=null) {
                if (!child.prev.equals(node)) return false;
                if (!validate(child,keys)) return false;
            } else {
                if (!node.equals(tail)) return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Queue<T> toQueue() {
            return (new JavaCompatibleLinkedQueue<T>(this));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Collection<T> toCollection() {
            return (new JavaCompatibleLinkedQueue<T>(this));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Node<T> node = head;
            while (node != null) {
                builder.append(node.value).append(", ");
                node = node.next;
            }
            return builder.toString();
        }

        private static class Node<T> {

            private T value = null;
            private Node<T> prev = null;
            private Node<T> next = null;

            private Node(T value) {
                this.value = value;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return "value=" + value + " previous=" + ((prev != null) ? prev.value : "NULL") + " next=" + ((next != null) ? next.value : "NULL");
            }
        }
    }

    public static class JavaCompatibleArrayQueue<T> extends java.util.AbstractQueue<T> {

        private ArrayQueue<T> queue = null;

        public JavaCompatibleArrayQueue(ArrayQueue<T> queue) {
            this.queue = queue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean add(T value) {
            return queue.offer(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object value) {
            return queue.remove((T)value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object value) {
            return queue.contains((T)value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean offer(T value) {
            return queue.offer(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T peek() {
            return queue.peek();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T poll() {
            return queue.poll();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return queue.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Iterator<T> iterator() {
            return (new ArrayQueueIterator<T>(queue));
        }

        private static class ArrayQueueIterator<T> implements java.util.Iterator<T> {

            private ArrayQueue<T> queue = null;
            private int last = -1;
            private int index = 0; //offset from first

            private ArrayQueueIterator(ArrayQueue<T> queue) {
                this.queue = queue;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return ((queue.firstIndex+index) < queue.lastIndex);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public T next() {
                if (queue.firstIndex+index < queue.lastIndex) {
                    last = queue.firstIndex+index;
                    return queue.array[queue.firstIndex+index++];
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                queue.remove(last);
            }
        }
    }

    public static class JavaCompatibleLinkedQueue<T> extends java.util.AbstractQueue<T> {

        private LinkedQueue<T> queue = null;

        public JavaCompatibleLinkedQueue(LinkedQueue<T> queue) {
            this.queue = queue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean add(T value) {
            return queue.offer(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object value) {
            return queue.remove((T)value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object value) {
            return queue.contains((T)value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean offer(T value) {
            return queue.offer(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T peek() {
            return queue.peek();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T poll() {
            return queue.poll();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return queue.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.Iterator<T> iterator() {
            return (new LinkedQueueIterator<T>(queue));
        }

        private static class LinkedQueueIterator<T> implements java.util.Iterator<T> {

            private LinkedQueue<T> queue = null;
            private LinkedQueue.Node<T> lastNode = null;
            private LinkedQueue.Node<T> nextNode = null;

            private LinkedQueueIterator(LinkedQueue<T> queue) {
                this.queue = queue;
                this.nextNode = queue.tail;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return (nextNode!=null);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public T next() {
                LinkedQueue.Node<T> current = nextNode;
                lastNode = current;
                if (current!=null) {
                    nextNode = current.prev;
                    return current.value;
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                queue.remove(lastNode);
            }
        }
    }
}
