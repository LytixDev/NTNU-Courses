public class NyString {
    String s;

    public NyString(String s) {
        this.s = s;
    }

    public static void main(String[] args) {
        NyString a = new NyString("denne setningen kan forkortes");
        System.out.println(a);    
        System.out.println("Forkortet :");
        a.simplify();
        System.out.println(a);    
        
        NyString b = new NyString("denne setningen kan forkortes");
        System.out.println(b);
        b.remove('e');
        System.out.println(b);    
    }

    public void simplify() {
        String[] words = this.s.split(" "); 
        StringBuilder newS = new StringBuilder();

        for (String str : words) {
            newS.append(str.charAt(0));
        }

        this.s = newS.toString();
    }

    public void remove(char toRemove) {
        StringBuilder newS = new StringBuilder();
        int prev = 0;
        for (int i=0; i < this.s.length(); i++) {
            if (this.s.charAt(i) == toRemove) {
                newS.append(this.s.substring(prev, i));
                prev = i+1;
            }
        }   

        newS.append(this.s.substring(prev, s.length()));
        this.s = newS.toString();
    }

    @Override
    public String toString() {
        return this.s;
    }
}
