import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The Class containing the SubImage and Location of the Original Image
 * @author Ritvik Verma 3035453610
 */
class ImagePanel extends JPanel implements Serializable{
	private JLabel jl;
	private int originalLocation;
	private int numb;
	/**
	 * Initiate Class containing the SubImages and location of the Original Image
	 * @param jl
	 * @param originalLocation
	 */
	ImagePanel(JLabel jl, int originalLocation, int numb){
		this.originalLocation=originalLocation;
		this.jl=jl;
		this.numb=numb;
	}
	ImagePanel(JLabel jl, int originalLocation){
		this.originalLocation=originalLocation;
		this.jl=jl;
	}
	/**
	 * To get the JLabel containing The sub Image
	 * @return The JLabel
	 */
	JLabel getJl() {
		return jl;
	}
	/**
	 * To get the original Location of the SubImage
	 * @return The location of the SubImage
	 */
	int getOriginalLocation() {
		return originalLocation;
	}
	/**
	 * To get the Number of the Image
	 * @return The Number of the Image
	 */
	int getNumb() {
		return numb;
	}
	
	/**
	 * @param originalLocation the originalLocation to set of the block
	 */
	void setOriginalLocation(int originalLocation) {
		this.originalLocation = originalLocation;
	}
}
