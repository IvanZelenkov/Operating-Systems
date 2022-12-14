import java.util.ArrayList;
import java.util.List;

/**
 * A node of any type. A node contains a data and links to its children and its parent.
 * @param <T> The class type of the node.
 */
public class Node<T> {
    private T level;
    private T pid;
    private T ppid;
    private List<Node<T>> children;
    private Node<T> parent;

    public Node(T level, T pid, T ppid) {
        this.level = level;
        this.pid = pid;
        this.ppid = ppid;
        this.children = new ArrayList<>();
    }

    /**
     * Add a child to this node.
     * @param child child node
     */
    public void addChild(Node<T> child) {
        child.setParent(this);
        children.add(child);
    }

    /**
     * Add a child node at the given index.
     * @param index The index at which the child has to be inserted.
     * @param child The child node.
     */
    public void addChildAt(int index, Node<T> child) {
        child.setParent(this);
        this.children.add(index, child);
    }

    public void setChildren(List<Node<T>> children) {
        for (Node<T> child : children)
            child.setParent(this);

        this.children = children;
    }

    /**
     * Remove all children of this node.
     */
    public void removeChildren() {
        this.children.clear();
    }

    /**
     * Remove child at given index.
     * @param index The index at which the child has to be removed.
     * @return the removed node.
     */
    public Node<T> removeChildAt(int index) {
        return children.remove(index);
    }

    /**
     * Remove given child of this node.
     * @param childToBeDeleted the child node to remove.
     * @return <code>true</code> if the given node was a child of this node and was deleted,
     * <code>false</code> otherwise.
     */
    public boolean removeChild(Node<T> childToBeDeleted) {
        List<Node<T>> list = getChildren();
        return list.remove(childToBeDeleted);
    }

    public T getLevel() {
        return level;
    }

    public void setLevel(T level) {
        this.level = level;
    }

    public T getPid() {
        return pid;
    }

    public void setPid(T pid) {
        this.pid = pid;
    }

    public T getPpid() {
        return ppid;
    }

    public void setPpid(T ppid) {
        this.ppid = ppid;
    }

    public Node<T> getParent() {
        return this.parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return this.children;
    }

    public Node<T> getChildAt(int index) {
        return children.get(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj)
            return false;

        if (obj instanceof Node)
            return ((Node<?>) obj).getPid().equals(this.ppid);

        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(this.level)
                .append(", ")
                .append(this.pid)
                .append(" ")
                .append(this.ppid).toString();
    }
}