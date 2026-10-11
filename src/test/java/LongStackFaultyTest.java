
import static org.junit.Assert.*;
import org.junit.Test;

/** Tests for error handling on faulty stacks and faulty expressions. */
public class LongStackFaultyTest {

   /** Runs r, expects a RuntimeException and returns its message. */
   private static String errorMessage (Runnable r) {
      RuntimeException e = assertThrows (RuntimeException.class, r::run);
      assertNotNull ("exception must have a message;", e.getMessage());
      return e.getMessage();
   }

   private static void assertContains (String msg, String expected) {
      assertTrue ("message \"" + msg + "\" must contain \"" + expected + "\";",
         msg.contains (expected));
   }

   // ---- pop / tos on an empty stack ----

   @Test (timeout=1000)
   public void testPopEmptyHasMessage() {
      LongStack m = new LongStack();
      assertContains (errorMessage (m::pop), "pop");
   }

   @Test (timeout=1000)
   public void testPopAfterEmptied() {
      LongStack m = new LongStack();
      m.push (1);
      m.pop();
      errorMessage (m::pop);
   }

   @Test (timeout=1000)
   public void testTosEmptyHasMessage() {
      LongStack m = new LongStack();
      assertContains (errorMessage (m::tos), "tos");
   }

   // ---- op underflow ----

   @Test (timeout=1000)
   public void testOpOnEmptyStack() {
      LongStack m = new LongStack();
      for (String s : new String[] {"+", "-", "*", "/"}) {
         assertContains (errorMessage (() -> m.op (s)), s);
      }
   }

   @Test (timeout=1000)
   public void testOpOnOneElement() {
      LongStack m = new LongStack();
      m.push (42);
      String msg = errorMessage (() -> m.op ("+"));
      assertContains (msg, "+");
      assertContains (msg, "42");
   }

   @Test (timeout=1000)
   public void testOpUnderflowKeepsStack() {
      LongStack m = new LongStack();
      m.push (42);
      errorMessage (() -> m.op ("-"));
      assertEquals ("failed op must not change the stack;", "42", m.toString());
      assertEquals ("failed op must not change the stack;", 42, m.tos());
   }

   // ---- op with illegal operator ----

   @Test (timeout=1000)
   public void testOpIllegalOperator() {
      LongStack m = new LongStack();
      m.push (1);
      m.push (2);
      for (String s : new String[] {"x", "%", "++", "", " + ", "SWAP"}) {
         assertContains (errorMessage (() -> m.op (s)), "\"" + s + "\"");
      }
      assertEquals ("illegal op must not change the stack;", "1 2", m.toString());
   }

   @Test (timeout=1000)
   public void testOpNullOperator() {
      LongStack m = new LongStack();
      m.push (1);
      m.push (2);
      errorMessage (() -> m.op (null));
      assertEquals ("null op must not change the stack;", "1 2", m.toString());
   }

   @Test (timeout=1000)
   public void testOpIllegalOperatorOnEmptyStack() {
      LongStack m = new LongStack();
      assertContains (errorMessage (() -> m.op ("x")), "x");
   }

   // ---- division by zero ----

   @Test (timeout=1000)
   public void testDivisionByZero() {
      LongStack m = new LongStack();
      m.push (7);
      m.push (0);
      String msg = errorMessage (() -> m.op ("/"));
      assertContains (msg, "7");
      assertContains (msg, "0");
      assertEquals ("division by zero must not change the stack;",
         "7 0", m.toString());
   }

   // ---- clone / equals / toString on faulty stacks ----

   @Test (timeout=1000)
   public void testCloneUnaffectedByFailedOp() throws CloneNotSupportedException {
      LongStack m1 = new LongStack();
      m1.push (5);
      LongStack m2 = (LongStack) m1.clone();
      errorMessage (() -> m1.op ("*"));
      assertEquals ("clone must equal original after failed op;", m1, m2);
   }

   @Test (timeout=1000)
   public void testCloneEmpty() throws CloneNotSupportedException {
      LongStack m1 = new LongStack();
      LongStack m2 = (LongStack) m1.clone();
      assertEquals ("clone of empty stack must be equal;", m1, m2);
      m2.push (1);
      assertNotEquals ("clone of empty stack must be independent;", m1, m2);
   }

   @Test (timeout=1000)
   public void testEqualsNotDependentOnToString() {
      LongStack m1 = new LongStack();
      LongStack m2 = new LongStack();
      m1.push (1);
      m1.push (23);
      m2.push (12);
      m2.push (3);
      assertNotEquals ("1 23 and 12 3 must not be equal;", m1, m2);
      assertNotEquals ("stack must not equal a string;", m1, "1 23");
      assertNotEquals ("stack must not equal null;", m1, null);
   }

   @Test (timeout=1000)
   public void testToStringMultiDigitAndNegative() {
      LongStack m = new LongStack();
      m.push (12);
      m.push (-345);
      m.push (Long.MIN_VALUE);
      assertEquals ("12 -345 " + Long.MIN_VALUE, m.toString());
   }

   @Test (timeout=1000)
   public void testToStringEmpty() {
      assertEquals ("", new LongStack().toString());
   }

   // ---- interpret: all four error types must contain pol ----

   @Test (timeout=1000)
   public void testInterpretNoExpression() {
      errorMessage (() -> LongStack.interpret (null));
      for (String pol : new String[] {"", " ", "\t", " \t \n "}) {
         assertContains (errorMessage (() -> LongStack.interpret (pol)), pol);
      }
   }

   @Test (timeout=1000)
   public void testInterpretTooManyNumbers() {
      for (String pol : new String[] {"5 2 - 7", "1 2", "1 2 3 +", "  4 4  "}) {
         assertContains (errorMessage (() -> LongStack.interpret (pol)), pol);
      }
   }

   @Test (timeout=1000)
   public void testInterpretIllegalSymbol() {
      for (String pol : new String[] {"67 xxx +", "2 x", "1 2 %", "1.5 2 +",
            "3 4 ++", "abc", "1 2 + -x", "99999999999999999999 1 +"}) {
         assertContains (errorMessage (() -> LongStack.interpret (pol)), pol);
      }
   }

   @Test (timeout=1000)
   public void testInterpretTooFewNumbers() {
      for (String pol : new String[] {"3 4 + - 5", "+", "1 -", "1 2 + *",
            "- 1 2"}) {
         assertContains (errorMessage (() -> LongStack.interpret (pol)), pol);
      }
   }

   @Test (timeout=1000)
   public void testInterpretDivisionByZero() {
      String pol = "5 3 3 - /";
      assertContains (errorMessage (() -> LongStack.interpret (pol)), pol);
   }

   @Test (timeout=1000)
   public void testInterpretNegativeNumbersAreNotOperators() {
      assertEquals (-7, LongStack.interpret ("-3 -4 +"));
      assertEquals (1, LongStack.interpret ("-3 -4 -"));
   }

}
