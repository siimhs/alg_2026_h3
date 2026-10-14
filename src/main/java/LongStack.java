import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Sources: Java API documentation;
 * implementation discussed and reviewed with Claude AI assistant.
 */
public class LongStack {

   private LinkedList<Long> stack;

   public static void main (String[] argum) {
      LongStack s = new LongStack();
      s.push(3);
      s.push(4);
      s.op("+");
      System.out.println(s + "  (oodatud: 7)");
      System.out.println(interpret("2 5 - 3 *") + "  (oodatud: -9)");
      System.out.println(interpret("35 10 -3 + /") + "  (oodatud: 5)");
   }

   LongStack() {
      stack = new LinkedList<>();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack copy = new LongStack();
      copy.stack = new LinkedList<Long>(stack);
      return copy;
   }

   public boolean stEmpty() {
      return stack.isEmpty();
   }

   public void push (long a) {
      stack.add(a);
   }

   public long pop() {
      if (stEmpty()) throw new RuntimeException("Cannot pop: stack is empty");
      return stack.removeLast();
   } // pop

   public void op (String s) {
      if (s == null) {
         throw new RuntimeException("Illegal operation: null");
      }
      if (stack.size() < 2) {
         throw new RuntimeException("Cannot perform operation \"" + s
                 + "\": two elements needed but stack is \"" + this + "\"");
      }

      long b = stack.getLast();
      long a = stack.get(stack.size() - 2);
      long result;

      switch (s) {
         case "+": result = a + b; break;
         case "-": result = a - b; break;
         case "*": result = a * b; break;
         case "/":
            if (b == 0) {
               throw new RuntimeException("Division by zero in operation \"" + s
                       + "\", stack is \"" + this + "\"");
            }
            result = a / b;
            break;
         default:
            throw new RuntimeException("Illegal operation \"" + s
                    + "\", allowed operations are + - * /");
      }

      stack.removeLast();
      stack.removeLast();
      stack.addLast(result);
   }

   public long tos() {
      if (stEmpty()) throw new RuntimeException("Cannot read top: stack is empty");
      return stack.getLast();
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (!(o instanceof LongStack)) return false;

      LongStack other = (LongStack) o;
      return stack.equals(other.stack);
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder();
      for (Long l : stack) {
         if (result.length() != 0) result.append(" ");
         result.append(l);
      }
      return result.toString();
   }

   public static long interpret (String pol) {
      if (pol == null || pol.trim().isEmpty()) {
         throw new RuntimeException("Expression is missing: \"" + pol + "\"");
      }

      LongStack st = new LongStack();
      LongStack rs = new LongStack();
      StringTokenizer tokens = new StringTokenizer(pol);

      while (tokens.hasMoreTokens()) {
         String token = tokens.nextToken();

         if (token.equals(">R")) {
            if (st.stEmpty()) {
               throw new RuntimeException("Cannot perform \">R\": data stack is empty"
                       + " in expression \"" + pol + "\"");
            }
            rs.push(st.pop());
         } else if (token.equals("R>")) {
            if (rs.stEmpty()) {
               throw new RuntimeException("Cannot perform \"R>\": return stack is empty"
                       + " in expression \"" + pol + "\"");
            }
            st.push(rs.pop());
         } else if (token.equals("R@")) {
            if (rs.stEmpty()) {
               throw new RuntimeException("Cannot perform \"R@\": return stack is empty"
                       + " in expression \"" + pol + "\"");
            }
            st.push(rs.tos());
         } else if (isOperator(token)) {
            if (st.stack.size() < 2) {
               throw new RuntimeException("Not enough numbers for operation \"" + token
                       + "\" in expression \"" + pol + "\"");
            }
            st.op(token);
         } else {
            try {
               st.push(Long.parseLong(token));
            } catch (NumberFormatException e) {
               throw new RuntimeException("Illegal symbol \"" + token
                       + "\" in expression \"" + pol + "\"");
            }
         }
      }

      if (!rs.stEmpty()) {
         throw new RuntimeException("Return stack is not empty at the end of expression \""
                 + pol + "\"");
      }
      if (st.stack.size() > 1) {
         throw new RuntimeException("Too many numbers in expression \"" + pol + "\"");
      }
      return st.pop();
   }

   private static boolean isOperator (String token) {
      return token.length() == 1 && "+-*/".indexOf(token.charAt(0)) >= 0;
   }

}
