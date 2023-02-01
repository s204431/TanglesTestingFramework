package main;

//Custom BitSet implementation with fast intersection.
public class BitSet {
    protected long[] set;
    private int size = 0;

    //Creates BitSet with specified maximum size.
    public BitSet(int maxSize) {
        set = new long[(maxSize-1)/64+1];
        size = maxSize;
    }

    //Adds participant with specific index to set (constant time complexity).
    public void add(int index) {
        int longIndex = index >> 6; //index / 64
        index = index & 63; //index % 64
        set[longIndex] = set[longIndex] | (((long)1) << index);
    }

    //Returns true if given index is part of the set.
    public boolean get(int index) {
        int longIndex = index >> 6; //index / 64
        index = index & 63; //index % 64
        return (set[longIndex] & (((long)1) << index)) > 0;
    }

    public int size() {
        return size;
    }

    //Requires same maximum size. partOfSet specifies if false or true means part of the set.
    public static int intersection(BitSet set1, BitSet set2, boolean partOfSet1, boolean partOfSet2) {
        int count = 0;
        for (int i = 0; i < set1.set.length; i++) {
            int amountFlipped = 0;
            long long1 = set1.set[i];
            long long2 = set2.set[i];
            if (!partOfSet1) {
                long1 = ~long1;
                amountFlipped++;
            }
            if (!partOfSet2) {
                long2 = ~long2;
                amountFlipped++;
            }
            count += Long.bitCount(long1 & long2);
            if (i == set1.set.length-1 && amountFlipped == 2) { //Last part of last long is not part of the set.
                int bitsInLastLong = set1.size() % 64;
                if (bitsInLastLong > 0) {
                    count -= 64 - bitsInLastLong;
                }
            }
        }
        return count;
    }

    public static int intersection(BitSet set1, BitSet set2, BitSet set3, boolean partOfSet1, boolean partOfSet2, boolean partOfSet3) {
        int count = 0;
        for (int i = 0; i < set1.set.length; i++) {
            int amountFlipped = 0;
            long long1 = set1.set[i];
            long long2 = set2.set[i];
            long long3 = set3.set[i];
            if (!partOfSet1) {
                long1 = ~long1;
                amountFlipped++;
            }
            if (!partOfSet2) {
                long2 = ~long2;
                amountFlipped++;
            }
            if (!partOfSet3) {
                long3 = ~long3;
                amountFlipped++;
            }
            count += Long.bitCount(long1 & long2 & long3);
            if (i == set1.set.length-1 && amountFlipped == 3) { //Last part of last long is not part of the set.
                int bitsInLastLong = set1.size() % 64;
                if (bitsInLastLong > 0) {
                    count -= 64 - bitsInLastLong;
                }
            }
        }
        return count;
    }

    public void print() {
        System.out.println(size + " " + set.length);
        for (long l : set) {
            for (int i = 0; i < Long.numberOfLeadingZeros(l); i++) {
                System.out.print("0");
            }
            System.out.print(Long.toBinaryString(l));
        }
        System.out.println();
    }

}
