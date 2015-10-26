package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.BatchRenderBackendInternal;
import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;


public class Lwjgl3BatchRenderBackendFactory {

	public static BatchRenderBackend create(long windowId) {
		return new BatchRenderBackendInternal(
			new Lwjgl3GL(),
			new Lwjgl3BufferFactory(),
			new Lwjgl3ImageFactory(),
			new Lwjgl3MouseCursorFactory(windowId));
	}

	public static BatchRenderBackend createCoreProfile(long windowId) {
		return new BatchRenderBackendInternal(
			new Lwjgl3CoreGL(),
			new Lwjgl3BufferFactory(),
			new Lwjgl3ImageFactory(),
			new Lwjgl3MouseCursorFactory(windowId));
	}
}

