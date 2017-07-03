package hu.ppke.itk.nlpg.purepos.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {

	@Test
	public void testThreadSafety() throws Exception {
		final Thread t1 = new Thread("long") {
			@Override
			public void run() {
				AnalysisQueue analysisQueue = new AnalysisQueue();
				while (!isInterrupted()) {
					analysisQueue.init(200000);
				}
			}
		};
		final Thread t2 = new Thread("short") {
			@Override
			public void run() {
				AnalysisQueue analysisQueue = new AnalysisQueue();
				while (!isInterrupted()) {
					analysisQueue.init(1);
				}
			}
		};

		final Map<Thread, Throwable> exceptions = new ConcurrentHashMap();

		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
			@Override
			@SuppressWarnings("ThrowableResultIgnored")
			public void uncaughtException(Thread t, Throwable e) {
				t1.interrupt();
				t2.interrupt();
				exceptions.put(t, e);
			}
		};

		t1.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		t2.setUncaughtExceptionHandler(uncaughtExceptionHandler);

		try {
			t1.start();
			t2.start();

			int waitSec = 5;
			t1.join(waitSec * 1000);
			t2.join(waitSec * 1000);

			Set<Map.Entry<Thread, Throwable>> entrySet = exceptions.entrySet();
			if (entrySet.isEmpty()) {
				System.out.println("No exception thrown in " + waitSec + " seconds: OK");
			} else {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(buffer);
				for (Map.Entry<Thread, Throwable> entry : entrySet) {
					Throwable e = entry.getValue();
					ps.print("Thread ");
					ps.print(entry.getKey().getName());
					ps.print(" thrown ");
					ps.println(e.getClass().getName());
					e.printStackTrace(ps);
					ps.println();
				}
				ps.close();
				fail(buffer.toString());
			}
		} finally {
			t1.interrupt();
			t2.interrupt();
		}
	}

}
