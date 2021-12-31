package fr.alexdoru.megawallsenhancementsmod.utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

// https://github.com/Moulberry/Hychat/blob/ebc70c6023f158f5a833894088e8da91771bcf7d/src/main/java/io/github/moulberry/hychat/core/util/MiscUtils.java
public class ClipboardUtil {

    public static void copyString(String msg) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null);
    }

//	public static void copyToClipboard(BufferedImage bufferedImage) {
//
//		if(SystemUtils.IS_OS_WINDOWS) {
//			try {
//				int width = bufferedImage.getWidth();
//				int height = bufferedImage.getHeight();
//
//				byte hdrSize = 0x28;
//				ByteBuffer buffer = ByteBuffer.allocate(hdrSize + width*height*4);
//				buffer.order(ByteOrder.LITTLE_ENDIAN);
//				//Header size
//				buffer.putInt(hdrSize);
//				//Width
//				buffer.putInt(width);
//				//Int32 biHeight;
//				buffer.putInt(height);
//				//Int16 biPlanes;
//				buffer.put((byte)1);
//				buffer.put((byte)0);
//				//Int16 biBitCount;
//				buffer.put((byte)32);
//				buffer.put((byte)0);
//				//Compression
//				buffer.putInt(0);
//				//Int32 biSizeImage;
//				buffer.putInt(width*height*4);
//
//				buffer.putInt(0);
//				buffer.putInt(0);
//				buffer.putInt(0);
//				buffer.putInt(0);
//
//				//Image data
//				for(int y=0; y<height; y++) {
//					for(int x=0; x<width; x++) {
//						int argb = bufferedImage.getRGB(x, height - y - 1);
//						if(((argb >> 24) & 0xFF) == 0) {
//							buffer.putInt(0x00000000);
//						} else {
//							buffer.putInt(argb);
//						}
//					}
//				}
//
//				buffer.flip();
//
//				byte hdrSizev5 = 0x7C;
//				ByteBuffer bufferv5 = ByteBuffer.allocate(hdrSizev5 + width*height*4);
//				bufferv5.order(ByteOrder.LITTLE_ENDIAN);
//				//Header size
//				bufferv5.putInt(hdrSizev5);
//				//Width
//				bufferv5.putInt(width);
//				//Int32 biHeight;
//				bufferv5.putInt(height);
//				//Int16 biPlanes;
//				bufferv5.put((byte)1);
//				bufferv5.put((byte)0);
//				//Int16 biBitCount;
//				bufferv5.put((byte)32);
//				bufferv5.put((byte)0);
//				//Compression
//				bufferv5.putInt(0);
//				//Int32 biSizeImage;
//				bufferv5.putInt(width*height*4);
//
//				bufferv5.putInt(0);
//				bufferv5.putInt(0);
//				bufferv5.putInt(0);
//				bufferv5.putInt(0);
//
//				bufferv5.order(ByteOrder.BIG_ENDIAN);
//				bufferv5.putInt(0xFF000000);
//				bufferv5.putInt(0x00FF0000);
//				bufferv5.putInt(0x0000FF00);
//				bufferv5.putInt(0x000000FF);
//				bufferv5.order(ByteOrder.LITTLE_ENDIAN);
//
//				//BGRs
//				bufferv5.put((byte)0x42);
//				bufferv5.put((byte)0x47);
//				bufferv5.put((byte)0x52);
//				bufferv5.put((byte)0x73);
//
//				for(int i=bufferv5.position(); i<hdrSizev5; i++) {
//					bufferv5.put((byte)0);
//				}
//
//				//Image data
//				for(int y=0; y<height; y++) {
//					for(int x=0; x<width; x++) {
//						int argb = bufferedImage.getRGB(x, height - y - 1);
//
//						int a = (argb >> 24) & 0xFF;
//						int r = (argb >> 16) & 0xFF;
//						int g = (argb >> 8) & 0xFF;
//						int b = argb & 0xFF;
//
//						r = r*a/0xFF;
//						g = g*a/0xFF;
//						b = b*a/0xFF;
//
//						bufferv5.putInt((a << 24) | (r << 16) | (g << 8) | b);
//					}
//				}
//
//				bufferv5.flip();
//
//				SunClipboard clip = (SunClipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
//
//				DataTransferer dt = DataTransferer.getInstance();
//				Field f = dt.getClass().getDeclaredField("CF_DIB");
//				f.setAccessible(true);
//				long format = f.getLong(null);
//
//				Method openClipboard = clip.getClass().getDeclaredMethod("openClipboard", SunClipboard.class);
//				openClipboard.setAccessible(true);
//				openClipboard.invoke(clip, clip);
//
//				Method publishClipboardData = clip.getClass().getDeclaredMethod("publishClipboardData",  long.class, byte[].class);
//				publishClipboardData.setAccessible(true);
//
//				byte[] arr = buffer.array();
//				publishClipboardData.invoke(clip, format, arr);
//
//				Method closeClipboard = clip.getClass().getDeclaredMethod("closeClipboard");
//				closeClipboard.setAccessible(true);
//				closeClipboard.invoke(clip);
//
//				return;
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		TransferableImage trans = new TransferableImage(bufferedImage);
//
//		try {
//			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
//		} catch(Exception e2) {
//			e2.printStackTrace();
//		}
//
//	}
//
//	private static class SimpleTransferable implements Transferable {
//		private final DataFlavor flavor;
//		private final Object object;
//
//		public SimpleTransferable(DataFlavor flavor, Object object) {
//			this.flavor = flavor;
//			this.object = object;
//		}
//
//		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
//			if (isDataFlavorSupported(flavor)) {
//				return object;
//			}
//			throw new UnsupportedFlavorException(flavor);
//		}
//
//		public DataFlavor[] getTransferDataFlavors() {
//			return new DataFlavor[] { flavor };
//		}
//
//		public boolean isDataFlavorSupported(DataFlavor flavor) {
//			return this.flavor.equals(flavor);
//		}
//	}
//
//	private static class TransferableImage extends SimpleTransferable {
//		public TransferableImage(BufferedImage image) {
//			super(DataFlavor.imageFlavor, image);
//		}
//	}
//	
//	private void screenshotFramebuffer(Framebuffer framebuffer) {
//        int w = framebuffer.framebufferWidth;
//        int h = framebuffer.framebufferHeight;
//
//        int i = w * h;
//        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(i);
//        int[] pixelValues = new int[i];
//
//        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
//        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
//
//        GlStateManager.bindTexture(framebuffer.framebufferTexture);
//        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
//
//        pixelBuffer.get(pixelValues); //Load buffer into array
//        TextureUtil.processPixelValues(pixelValues, w, h); //Flip vertically
//        BufferedImage bufferedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//        int j = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight;
//
//        for (int k = j; k < framebuffer.framebufferTextureHeight; ++k) {
//            for (int l = 0; l < framebuffer.framebufferWidth; ++l) {
//                bufferedimage.setRGB(l, k - j, pixelValues[k * framebuffer.framebufferTextureWidth + l]);
//            }
//        }
//
//        copyToClipboard(bufferedimage);
//    }
//	
//	private void screenshotChat(List<ExtendedChatLine> chatLinesWrapped, int scrollPos) {
//        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
//        int chatWidth = chatBox.getChatWidth(scaledResolution);
//        int chatHeight = chatBox.getChatHeight(scaledResolution);
//        float chatScale = chatBox.getChatScale();
//        int baseScaleFactor = chatScale <= 0.5 ? 1 : 2;
//        float chatScaleFactor = baseScaleFactor/chatScale;
//
//        float lineHeight = 9*chatScale;
//
//        int maxChatLineCount = (int)Math.floor(chatHeight/lineHeight);
//        float maxLineWidth = 10;
//        int lines = 0;
//        for (int lineIndex = 0; lineIndex < maxChatLineCount; ++lineIndex) {
//            if (lineIndex + scrollPos >= chatLinesWrapped.size()) {
//                break;
//            }
//            ChatLine chatline = chatLinesWrapped.get(lineIndex + scrollPos);
//            if(cleanColour(chatline.getChatComponent().getUnformattedText()).trim().length() > 0) {
//                lines = lineIndex+1;
//            }
//            maxLineWidth = Math.max(maxLineWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(
//                    chatline.getChatComponent().getFormattedText())*chatScale);
//        }
//        int w = (int)Math.ceil((maxLineWidth+8)*chatScaleFactor);
//        int h = (int)Math.ceil((lines*lineHeight+4)*chatScaleFactor);
//
//        Framebuffer framebuffer = createBindFramebuffer(w, h);
//
//        String bg = chatBox.getBackgroundColour();
//        chatBox.setBackgroundColour("0:0:54:57:63");
//        //chatBox.setBackgroundColour("0:0:0:0:0");
//        GlStateManager.translate(0, h-2*chatScaleFactor, 0);
//        GlStateManager.scale(chatScaleFactor, chatScaleFactor, 1);
//        renderChat(chatLinesWrapped, -1, -1, 0, 4, 0, lines,
//                scrollPos, true, lineHeight, chatWidth, chatScale, false);
//        chatBox.setBackgroundColour(bg);
//
//        screenshotFramebuffer(framebuffer);
//
//        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
//        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
//    }
//	
//	public static String cleanColour(String in) {
//        return in.replaceAll("(?i)\\u00A7.", "");
//    }
//	
//	private Framebuffer createBindFramebuffer(int w, int h) {
//        Framebuffer framebuffer = new Framebuffer(w, h, false);
//        framebuffer.framebufferColor[0] = 0x36/255f;
//        framebuffer.framebufferColor[1] = 0x39/255f;
//        framebuffer.framebufferColor[2] = 0x3F/255f;
//        framebuffer.framebufferClear();
//
//        GlStateManager.matrixMode(5889);
//        GlStateManager.loadIdentity();
//        GlStateManager.ortho(0.0D, w, h, 0.0D, 1000.0D, 3000.0D);
//        GlStateManager.matrixMode(5888);
//        GlStateManager.loadIdentity();
//        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
//
//        framebuffer.bindFramebuffer(true);
//
//        return framebuffer;
//    }

}
