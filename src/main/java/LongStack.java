/*
 * This file was written with the help of Claude, an AI assistant by Anthropic
 * (model: Claude Opus 5.5, claude-opus-5-5), used via Claude Code.
 */

import java.util.*;

public class LongStack {

   /** Stack elements, bottom first, top last. */
   private final LinkedList<Long> stack = new LinkedList<>();

   public static void main (String[] argum) {
      System.out.println(interpret("35 10 -3 + /"));
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
      stack.addLast(a);
   }

   public long pop() {
      if (stEmpty()) {
         throw new RuntimeException("pop: cannot pop from an empty stack");
      }
      return stack.removeLast();
   }

   public void op (String s) {
      if (s == null || !(s.equals("+") || s.equals("-")
            || s.equals("*") || s.equals("/"))) {
         throw new RuntimeException("op: illegal operation \"" + s
               + "\", allowed are + - * /");
      }
      if (stack.size() < 2) {
         throw new RuntimeException("op: operation \"" + s
               + "\" needs 2 elements, but stack [" + this + "] has "
               + stack.size());
      }
      long b = pop();
      long a = pop();
      switch (s) {
         case "+":
            push(a + b);
            break;
         case "-":
            push(a - b);
            break;
         case "*":
            push(a * b);
            break;
         default:
            if (b == 0) {
               push(a);
               push(b);
               throw new RuntimeException("op: division by zero in \""
                     + a + " " + b + " /\"");
            }
            push(a / b);
      }
   }

   public long tos() {
      if (stEmpty()) {
         throw new RuntimeException("tos: cannot read top of an empty stack");
      }
      return stack.getLast();
   }

   @Override
   public boolean equals (Object o) {
      if (!(o instanceof LongStack)) {
         return false;
      }
      return stack.equals(((LongStack) o).stack);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      for (long l : stack) {
         if (sb.length() > 0) {
            sb.append(' ');
         }
         sb.append(l);
      }
      return sb.toString();
   }

   public static long interpret (String pol) {
      if (pol == null || pol.trim().isEmpty()) {
         throw new RuntimeException("interpret: expression is empty: \""
               + pol + "\"");
      }
      LongStack stack = new LongStack();
      for (String token : pol.trim().split("\\s+")) {
         if (token.equals("+") || token.equals("-")
               || token.equals("*") || token.equals("/")) {
            if (stack.stack.size() < 2) {
               throw new RuntimeException("interpret: not enough numbers for \""
                     + token + "\" in expression \"" + pol + "\"");
            }
            try {
               stack.op(token);
            } catch (RuntimeException e) {
               throw new RuntimeException("interpret: error in expression \""
                     + pol + "\": " + e.getMessage(), e);
            }
         } else {
            try {
               stack.push(Long.parseLong(token));
            } catch (NumberFormatException e) {
               throw new RuntimeException("interpret: illegal symbol \""
                     + token + "\" in expression \"" + pol + "\"");
            }
         }
      }
      if (stack.stack.size() > 1) {
         throw new RuntimeException("interpret: too many numbers in expression \""
               + pol + "\", stack left: [" + stack + "]");
      }
      return stack.pop();
   }

}
