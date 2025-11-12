import java.util.ArrayList;

public class LazyBST<T extends Comparable<? super T>> {
    protected Entry<T> root;
    protected int size;

    public LazyBST() {
        root = null;
        size = 0;
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
        sb.append(root.element).append(" (s: ").append(root.size).append(", h: ").append(root.height).append(")");

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
            sb.append(node.element).append(" (s: ").append(node.size).append(", h: ").append(node.height).append(")");

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
                comp = x.compareTo(temp.element);
                if (comp == 0) {
                    System.out.println("Word already in dictionary");
                    reduceSizes(temp);
                }
                if (comp < 0)
                    if (temp.left != null) {
                        temp = temp.left;
                    } else {
                        temp.left = new Entry<>(x, temp);
                        updateInfo(temp, temp.right == null);
                        handleRebalance(temp);
                        break;
                    } // temp.left == null
                else if (temp.right != null) {
                    temp = temp.right;
                } else {
                    temp.right = new Entry<>(x, temp);
                    updateInfo(temp, temp.left == null);
                    handleRebalance(temp);
                    break;
                } // temp.right == null
            } // while
        } // root not null
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

    public void updateInfo(Entry<T> x, boolean updateHeight) {
        // while (x.parent != null) {
        // x.size = (x.left != null ? x.left.size : 0) + (x.right != null ? x.right.size
        // : 0);
        // if (updateHeight)
        // x.height = Math.max(x.left != null ? x.left.height : 0, x.right != null ?
        // x.right.height : 0) + 1;
        // x = x.parent;
        // }
        // x.size += inc;
        // if (updateHeight)
        // x.height = Math.max(x.left != null ? x.left.height : 0, x.right != null ?
        // x.right.height : 0) + 1;

        while (x != null) {
            x.size = (x.left != null ? x.left.size : 0) + (x.right != null ? x.right.size : 0) + 1;
            if (updateHeight) {
                int newHeight = Math.max(
                        x.left != null ? x.left.height : -1,
                        x.right != null ? x.right.height : -1) + 1;
                if (newHeight == x.height)
                    break; // Height unchanged, stop
                x.height = newHeight;
            }
            x = x.parent;
        }
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
        // handleRebalance();
        return p.element;
    } // method deleteEntry

    protected void reduceSizes(Entry<T> p) {
        p.size--;
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

    protected void handleRebalance(Entry<T> x) {
        Entry<T> node = nodeToRebalance(x);
        if (node != null) {
            Entry<T> parent = node.parent;
            node = rebalance(node);
            if (parent != null) {
                if (node.element.compareTo(parent.element) < 0) {
                    parent.left = node;
                } else {
                    parent.right = node;
                }
                updateInfo(parent, true);
            } else {
                root = node;
            }
        }
    }

    protected Entry<T> nodeToRebalance(Entry<T> x) {
        Entry<T> prev;
        while (x.parent != null) {
            prev = x;
            x = x.parent;
            if (x.height <= 3) {
                continue;
            }
            if (!outOfBalance(x) && outOfBalance(prev)) {
                return prev;
            }
        }
        if (outOfBalance(x) && x.height > 3) {
            return x;
        }
        return null;
    }

    protected boolean outOfBalance(Entry<T> x) {
        return ((x.left != null ? x.left.size * 2 : 0) <= (x.right != null ? x.right.size : 0)
                || (x.left != null ? x.left.size : 0) >= (x.right != null ? x.right.size * 2 : 0));
    }

    protected Entry<T> rebalance(Entry<T> x) {
        ArrayList<Entry<T>> items = new ArrayList<>(x.size);
        nodeToArray(x, items);
        return refactorNodes(items, x.parent);
    }

    protected void nodeToArray(Entry<T> x, ArrayList<Entry<T>> a) {
        if (x.left != null)
            nodeToArray(x.left, a);
        a.add(x);
        if (x.right != null)
            nodeToArray(x.right, a);
    }

    protected Entry<T> refactorNodes(ArrayList<Entry<T>> x, Entry<T> parent) {
        int middle = (x.size() - 1) / 2;
        Entry<T> temp = resetNode(x.get(middle), parent);
        if (x.size() > 2) {
            Entry<T> left = refactorNodes(new ArrayList<>(x.subList(0, middle)), temp);
            Entry<T> right = refactorNodes(new ArrayList<>(x.subList(middle + 1, x.size())), temp);
            temp.left = left;
            temp.right = right;
            updateInfo(temp, true);
            return temp;
        } else if (x.size() == 2) {
            temp.right = resetNode(x.get(1), temp);
            updateInfo(temp, temp.left == null);
            return temp;
        } else {
            return temp;
        }
    }

    protected Entry<T> resetNode(Entry<T> x, Entry<T> parent) {
        x.parent = parent;
        x.left = null;
        x.right = null;
        x.height = 0;
        x.size = 1;
        return x;
    }

    protected static class Entry<T> {
        protected T element;
        protected int size = 1;
        protected int height = 0;

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