package Model;

public class DisJointSet
{
    int[] level, ancestor;
    int n;

    public DisJointSet(int n)
    {
        level = new int[n];
        ancestor = new int[n];
        this.n = n;
        makeSet();
    }

    public void makeSet()
    {
        // Initially, all elements are in their
        // own set.
        for (int i=0; i<n; i++)
            ancestor[i] = i;
    }

    // Finds the representative of the set that x
    // is an element of
    public int find(int x)
    {
        if (ancestor[x] != x)
        {
            // if x is not the ancestor of itself,
            // then x is not the representative of
            // its set.
            // so we recursively call Find on its ancestor
            // and move i's node directly under the
            // representative of this set
            return find(ancestor[x]);
        }

        return x;
    }

    // Unites the set that includes x and the set
    // that includes y
    public void union(int x, int y)
    {
        // Find the representatives (or the mother nodes)
        // for x an y
        int xMother = find(x);
        int yMother = find(y);

        // Elements are in the same set, no need
        // to unite anything.
        if (xMother == yMother)
            return;

        // If x's level is less than y's level
        // Then move x under y  so that depth of tree
        // remains less
        if (level[xMother] < level[yMother])
            ancestor[xMother] = yMother;

            // Else if y's level is less than x's level
            // Then move y under x so that depth of tree
            // remains less
        else if(level[yMother]<level[xMother])
            ancestor[yMother] = xMother;

        else  // Else if their levels are the same
        {
            // Then move y under x (doesn't matter
            // which one goes where)
            ancestor[yMother] = xMother;

            // And increment the the result tree's
            // level by 1
            level[xMother] = level[xMother] + 1;
        }
    }
}