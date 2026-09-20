import java.util.*;

public class LongStack {

   private final List<Long> stack = new ArrayList<>();
   public static void main (String[] argum) {
      // TODO!!! Your tests here!
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack result = new LongStack();
      for (long l : stack) {
         result.push(l);
      }
      return result;
   }

   public boolean stEmpty() {
      return stack.isEmpty();
   }

   public void push (long a) {
      stack.add(a);
   }

   public long pop() {
      return stack.removeLast();
   }

   public void op (String s) {
      switch (s) {
         case "+":
            push(pop() + pop());
            break;
         case "-":
            long a = pop();
            long b = pop();
            push(b - a);
            break;
         case "*":
            push(pop() * pop());
            break;
         case "/":
            long c = pop();
            long d = pop();
            push(d / c);
            break;
         default:
            throw new RuntimeException("Unknown operation: " + s);
      }
   }

   public long tos() {
      return stack.get(stack.size() - 1);
   }

   @Override
   public boolean equals (Object o) {
      if (o == null || !(o instanceof LongStack)) {
         return false;
      }
      LongStack other = (LongStack)o;
      return this.stack.equals(other.stack);
   }

   @Override
   public String toString() {
      String result = "";
      for (long l : stack) {
         result += l + " ";
      }
      return result;
   }

   public static long interpret (String pol) {
      String[] tokens = pol.trim().split("\\s+");
      LongStack stack = new LongStack();
      for (String token : tokens) {
         if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
            stack.op(token);
         } else {
            stack.push(Long.parseLong(token));
         }
      }
      long result = stack.pop();
      if (!stack.stEmpty()) {
         throw new RuntimeException("Stack not empty after interpretation");
      }
      return result;
   }

}
