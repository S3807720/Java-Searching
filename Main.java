    package phonebook;

    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.util.*;

    public class Main {
        private final static String DIRECTORY_FILE = "C:\\Users\\l\\Desktop\\Phone Book\\directory.txt";
        private final static String FIND_FILE = "C:\\Users\\l\\Desktop\\Phone Book\\find.txt";
        private static List<String> directory = new ArrayList<>();
        private static List<String> searching = new ArrayList<>();
        private static Hashtable<String, String> hashTable = new Hashtable<String, String>();
        public static long LinearDuration;

        public static void main(String[] args) throws InterruptedException {
            long sortTime = 0, searchTime, prevTime;

            directory = ReadFromFile(DIRECTORY_FILE);
            searching = ReadFromFile(FIND_FILE);

            // linear
            System.out.println("Start searching (linear search)...");
            int found = LinearFind();
            System.out.printf("Found %d / %d entries. Time taken: %s%n%n", found, searching.size(), ConvertTime(LinearDuration));
            System.out.println("Start searching (bubble sort + jump search)...");

            // bubble / jump
            prevTime = System.currentTimeMillis();
            boolean check = Sort();
            sortTime = System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            if(check) {
                found = JumpSearch();
            } else {
                found = LinearFind();
            }
            searchTime = System.currentTimeMillis() - prevTime;
            System.out.printf("Found %d / %d entries. Time taken: %s%n", found, searching.size(), ConvertTime(sortTime + searchTime));
            System.out.printf("Sorting time: %s", ConvertTime(sortTime));
            if (!check) System.out.print(" - STOPPED, moved to linear search");
            System.out.printf("%nSearching time: %s%n%n", ConvertTime(searchTime));

            // binary search
            System.out.println("Start searching (quick sort + binary search)...");
            directory = ReadFromFile(DIRECTORY_FILE); // reset list
            prevTime = System.currentTimeMillis();
            QuickSort(directory, 0, directory.size() - 1);
            sortTime = System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            found = BinarySearch();
            searchTime = System.currentTimeMillis() - prevTime;
            System.out.printf("Found %d / %d entries. Time taken: %s", found, searching.size(), ConvertTime(searchTime + sortTime));
            System.out.printf("%nSorting time: %s", ConvertTime(sortTime));
            System.out.printf("%nSearching time: %s%n%n", ConvertTime(searchTime));
            Write();

            // hash
            System.out.println("Start searching (hash table)...");
            prevTime = System.currentTimeMillis();
            CreateHashTable();
            sortTime = System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            found = HashSearch();
            searchTime = System.currentTimeMillis() - prevTime;
            System.out.printf("Found %d / %d entries. Time taken: %s", found, searching.size(), ConvertTime(searchTime + sortTime));
            System.out.printf("%nCreating time: %s", ConvertTime(sortTime));
            System.out.printf("%nSearching time: %s%n%n", ConvertTime(searchTime));
        }

        public static int HashSearch() {
            int found = 0;
            for (String s : searching) {
                if (hashTable.containsKey(s)) {
                    found++;
                }
            }
            return found;
        }

        public static void CreateHashTable() {
            File file = new File(DIRECTORY_FILE);
            String num = "", name = "";
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNext()) {
                    String[] temp = sc.nextLine().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    num = temp[0].trim();
                    name = temp[1].trim();
                    hashTable.put(name, num);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public static int BinarySearch() {
            int found = 0;
            for (String n : searching) {
                n = n.trim();
                int high = directory.size();
                int low = 0, mid = 0;
                String guess = "";
                while (low <= high) {
                    mid = (low + high) / 2;
                    guess = directory.get(mid).trim();
                    if (guess.equals(n)) {
                        found++;
                        break;
                    } else if (guess.compareToIgnoreCase(n) > 0) {
                        high = mid - 1;
                    } else if (guess.compareToIgnoreCase(n) < 0) {
                        low = mid + 1;
                    } else {
                        System.out.println("Something went wrong!");
                    }
                }
            }
            return found;
        }

        public static int JumpSearch() {
            int size = directory.size();
            int found = 0;
            for (String n : searching) {
                n = n.trim();
                int step = (int)Math.floor(Math.sqrt(size));
                int prev = step - 1;
                while (size > prev && directory.get(prev).trim().compareToIgnoreCase(n) < 0)  {
                    prev += step;
                }
                for (int i = prev - step + 1; prev >= i && size > i; i++) {
                    if (directory.get(i).trim().equals(n)) {
                        found++;
                        continue;
                    }
                }

            }
            return found;
        }

        public static int LinearFind() {
            long prevTime = System.currentTimeMillis();
            int found = 0;
            for(String s : searching) {
                for (String s2 : directory) {
                    if (s.trim().equals(s2.trim())) {
                        found++;
                    }
                }
            }
            LinearDuration = System.currentTimeMillis() - prevTime;
            return found;
        }

        public static boolean Sort() {
          //  Collections.sort(directory);
          //  Collections.sort(searching);
            int size = directory.size();
            String temp = "";
            boolean swapped;
            long prevTime = System.currentTimeMillis();
            for (int i = 0; i < size - 1; i++) {
                swapped = false;
                if (System.currentTimeMillis() - prevTime > LinearDuration * 10) {
                    return false;
                }
                for (int j = 0; j < size - i - 1; j++)  {
                    if (directory.get(j).compareToIgnoreCase(directory.get(j + 1)) > 0)  {
                        // swap j & j+1
                        temp = directory.get(j);
                        directory.set(j, directory.get(j+1));
                        directory.set(j+1, temp);
                        swapped = true;
                    }
                }
                // break if no elements swapped
                if (swapped == false)
                    break;
            }
            Write();
            return true;

        }

        // A utility function to swap two elements
        public static void Swap(List<String> arr, int low, int pivot) {
            String tmp = arr.get(low);
            arr.set(low, arr.get(pivot));
            arr.set(pivot, tmp);
        }

        public static int Partition(List<String> arr, int low, int high) {
            int p = low, j;
            for (j = low + 1; j <= high; j++) {
                if (arr.get(j).compareToIgnoreCase(arr.get(low)) < 0) {
                    //arr.get(j) < arr.get(low)
                    Swap(arr, ++p, j);
                }
            }
            Swap(arr, low, p);
            return p;
        }

        public static void QuickSort(List<String> arr, int low, int high) {
            if (low < high) {
                int p = Partition(arr, low, high);
                QuickSort(arr, low, p - 1);
                QuickSort(arr, p + 1, high);
            }
        }


        public static String ConvertTime(long duration) {
            long minutes = duration / (60 * 1000);
            long seconds = (duration % (60 * 1000)) / 1000;
            long ms = (duration % (60 * 1000)) % 1000;
            return String.format("%d min. %d sec. %d ms.", minutes, seconds, ms);
        }


        public static void Write() {
            try {
                BufferedWriter myWriter = new BufferedWriter(new FileWriter("sorted.txt"));
                for(String s : directory) {
                    myWriter.newLine();
                    myWriter.write(s);
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public static List<String> ReadFromFile(String fname) {
            File file = new File(fname);
            List<String> list = new ArrayList<>();
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNext()) {
                    list.add(sc.nextLine().replaceAll("\\d", ""));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return list;
        }
    }
