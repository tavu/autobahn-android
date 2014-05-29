package autobahn.android.utils;

/**
 * Created by Nl0st on 27/5/2014.
 */
public class Category<T> {

    private String name;
    private T children;

    public Category(String name, T children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getChildren() {
        return children;
    }

    public void setChildren(T children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Category name:" + name;
    }

}
