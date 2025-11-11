import java.util.ArrayList;
import java.util.Stack;

public class LazyBST<T extends Comparable<? super T>> extends BinarySearchTree<T> {
    protected Entry<T> root;

    public LazyBST() {
    }

    public int getSize() {
        return this.root.size;
    }

    public void printTree() {
        print(this.root);
    }

    public void print(Entry<T> x) {
        System.out.println(traversePreOrder(x));
    }

    public String traversePreOrder(Entry<T> root) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.element);

        String pointerRight = "└──";
        String pointerLeft = (root.right != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.left, root.right != null);
        traverseNodes(sb, "", pointerRight, root.right, false);

        return sb.toString();
    }

    public void traverseNodes(StringBuilder sb, String padding, String pointer, Entry<T> node,
            boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.element);

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerForRight = "└──";
            String pointerForLeft = (node.right != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerForLeft, node.left, node.right != null);
            traverseNodes(sb, paddingForBoth, pointerForRight, node.right, false);
        }
    }

    protected void printTree(Entry<T> e) {

        System.out.println(e.element.toString());
        System.out.println(e.size + " element(s)");
        if (e.left != null) {
            printTree(e.left);
        }
        if (e.right != null) {
            printTree(e.right);
        }
    }

    public void insert(T x) {
        if (root == null) {
            if (x == null)
                throw new NullPointerException();
            root = new Entry<>(x, null);
            size++;
        } // empty tree
        else {
            Entry<T> temp = root;
            int comp;

            while (true) {
                temp.size++;
                comp = x.compareTo(temp.element);
                if (comp == 0) {
                    temp.size--;
                    System.out.println("Word already in dictionary");
                    reduceSizes(temp);
                }
                if (comp < 0)
                    if (temp.left != null) {
                        temp = temp.left;
                    } else {
                        temp.left = new Entry<>(x, temp);
                        size++;
                        break;
                    } // temp.left == null
                else if (temp.right != null) {
                    temp = temp.right;
                } else {
                    temp.right = new Entry<>(x, temp);
                    size++;
                    break;
                } // temp.right == null
            } // while
        } // root not null
        handleRebalance();
    }

    public Entry<T> find(T x) {
        int comp;

        if (x == null)
            throw new NullPointerException();
        Entry<T> e = root;
        while (e != null) {
            comp = x.compareTo(e.element);
            if (comp == 0)
                return e;
            else if (comp < 0)
                e = e.left;
            else
                e = e.right;
        } // while
        return e;
    }

    public T remove(T x) {
        Entry<T> p = find(x);
        if (p == null) {
            return null;
        }

        // If p has two children, replace p's element with p's successor's
        // element, then make p reference that successor.
        if (p.left != null && p.right != null) {
            Entry<T> s = successor(p);
            p.element = s.element;
            p = s;
        } // p had two children

        // At this point, p has either no children or one child.

        Entry<T> replacement;

        if (p.left != null)
            replacement = p.left;
        else
            replacement = p.right;

        // If p has at least one child, link replacement to p.parent.
        if (replacement != null) {
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left = replacement;
            else
                p.parent.right = replacement;
        } // p has at least one child
        else if (p.parent == null)
            root = null;
        else {
            if (p == p.parent.left)
                p.parent.left = null;
            else
                p.parent.right = null;
        } // p has a parent but no children
        reduceSizes(p);
        handleRebalance();
        return p.element;
    } // method deleteEntry

    protected void reduceSizes(Entry<T> p) {
        Entry<T> parent = p.parent;
        while (parent != null) {
            parent.size--;
            parent = parent.parent;
        }
    }

    protected Entry<T> successor(Entry<T> e) {
        if (e == null)
            return null;
        else if (e.right != null) {
            // successor is leftmost Entry in right subtree of e
            Entry<T> p = e.right;
            while (p.left != null)
                p = p.left;
            return p;

        } // e has a right child
        else {

            // go up the tree to the left as far as possible, then go up
            // to the right.
            Entry<T> p = e.parent;
            Entry<T> ch = e;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            } // while
            return p;
        } // e has no right child
    } // method successor

    protected void handleRebalance() {
        Entry<T> rebalance = shouldRebalance(this.root);
        if (rebalance != null) {
            // print(this.root);
            if (rebalance.parent == null) {
                this.root = rebalance(rebalance);
            } else {
                int c = rebalance.element.compareTo(rebalance.parent.element);
                if (c < 0) {
                    rebalance.parent.left = rebalance(rebalance);
                } else {
                    rebalance.parent.right = rebalance(rebalance);
                }
            }
            // print(this.root);
        }
    }

    protected Entry<T> shouldRebalance(Entry<T> x) {
        if (x.getHeight() <= 3) {
            return null;
        }
        int leftSize = x.left != null ? x.left.size : 0;
        int rightSize = x.right != null ? x.right.size : 0;
        if (leftSize * 2 <= rightSize
                || leftSize >= rightSize * 2) {
            // System.out.println(x.element + " - L: " + leftSize + " R: " + rightSize);
            return x;
        }
        Entry<T> left = x.left != null ? shouldRebalance(x.left) : null;
        Entry<T> right = x.right != null ? shouldRebalance(x.right) : null;
        if (left != null) {
            return left;
        } else {
            return right;
        }
    }

    protected Entry<T> rebalance(Entry<T> x) {
        ArrayList<Entry<T>> items = new ArrayList<>(x.size);
        Stack<Entry<T>> s = new Stack<>();
        Entry<T> curr = x;

        while (curr != null || !s.isEmpty()) {
            while (curr != null) {
                s.push(curr);
                curr = curr.left;
            }
            curr = s.pop();
            items.add(curr);
            curr = curr.right;
        }

        items.sort((a, b) -> {
            return a.element.compareTo(b.element);
        });
        // items.forEach(j -> System.out.print(j.element + " "));
        // System.out.println();

        return refactorNodes(items, x.parent);
    }

    protected Entry<T> refactorNodes(ArrayList<Entry<T>> l, Entry<T> parent) {
        int middle = (l.size() - 1) / 2;
        Entry<T> temp = new Entry<>(l.get(middle).element, parent);
        if (l.size() > 2) {
            Entry<T> left = refactorNodes(new ArrayList<>(l.subList(0, middle)), temp);
            Entry<T> right = refactorNodes(new ArrayList<>(l.subList(middle + 1, l.size())), temp);
            temp.left = left;
            temp.right = right;
            temp.size = 1 + temp.left.size + temp.right.size;
            return temp;
        } else if (l.size() == 2) {
            temp.right = new Entry<>(l.get(1).element, temp);
            temp.size = 2;
            return temp;
        } else {
            return temp;
        }
    }

    protected static class Entry<T> {
        protected T element;
        protected int size = 1;

        protected Entry<T> left = null,
                right = null,
                parent;

        /**
         * Initializes this Entry object.
         *
         * This default constructor is defined for the sake of subclasses of
         * the BinarySearchTree class.
         */
        public Entry() {
        }

        public int getNodeCount() {
            if (left != null && right != null)
                return 1 + left.getNodeCount() + right.getNodeCount();
            else if (left != null)
                return 1 + left.getNodeCount();
            else if (right != null)
                return 1 + right.getNodeCount();
            else
                return 1;
        }

        public int getHeight() {
            int a, b;
            if (left != null && right != null)
                return 1 + ((a = left.getHeight()) > (b = right.getHeight()) ? a : b);
            else if (left != null)
                return 1 + left.getHeight();
            else if (right != null)
                return 1 + right.getHeight();
            else
                return 0;
        }

        /**
         * Initializes this Entry object from element and parent.
         *
         */
        public Entry(T element, Entry<T> parent) {
            this.element = element;
            this.parent = parent;
        } // constructor

    } // class Entry
}