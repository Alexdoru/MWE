package fr.alexdoru.configlib;

public interface IRendererManager {

    /**
     * Returns the IRenderer associated to this RendererPosition
     */
    IRenderer getRendererFromPosition(RendererPosition position);

    /**
     * Method called when rendering the background of the gui screen
     * used to edit the position of a renderer
     *
     * @param editedRenderer - the renderer currently edited
     */
    void renderEditScreenBackground(IRenderer editedRenderer);

}
