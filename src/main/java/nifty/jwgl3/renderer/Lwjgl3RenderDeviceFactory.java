package nifty.jwgl3.renderer;

import de.lessvoid.nifty.render.batch.BatchRenderBackendInternal;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;


public class Lwjgl3RenderDeviceFactory {


	public static BatchRenderDevice create(long windowId) {
		// TODO: choose the implementation by the current context version
		return new BatchRenderDevice(createNonCoreProfile(windowId));
	}

	public static BatchRenderBackend createNonCoreProfile(long windowId) {
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

