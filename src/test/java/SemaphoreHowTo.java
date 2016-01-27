import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SemaphoreHowTo {


	private List<Integer> numbers;
	private Semaphore semaphore;
	private boolean read;

	@Before
	public void setup() {
		read = true;
		if (numbers == null) {
			numbers = new ArrayList<>();
		}
		numbers.clear();
	}

	@Test
	public void howToSyncWithSemaphores() throws InterruptedException {

		semaphore = new Semaphore(1, false);

		Thread numGenerator = new Thread(() -> {
			for (int i = 1; i < 10; i++) {
 				genBigList(i);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		Thread numRetriever = new Thread(() -> {
			while (read) {

				try {
					semaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (numbers.size() > 0) {
					System.out.println("read");
					System.out.println(numbers);
					numbers.clear();
				}
				semaphore.release();
			}
		});
		read = true;
		numGenerator.start();
		numRetriever.start();


		try {
			numGenerator.join();
			read = false;
			numRetriever.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

	private  List<Integer> genBigList(int len)  {

		for (int j = 0; j < len; j++) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int width = 10;
			for (int i = width * j; i < ((j + 1) * width); i++) {
					numbers.add(i);
			}
			semaphore.release();
		}
		return numbers;
	}

}
