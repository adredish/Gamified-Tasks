package RatingPackage;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class RateByStars {
    private HBox layout = new HBox();
    private List<Button> stars = new ArrayList<Button>();
    private Button selectedStar = null;

   public RateByStars(int nStars) {
         for (int iB = 0; iB<nStars; iB++) {
             Button B = new Button();
            B.setText(String.valueOf(iB+1));
            B.setUserData(iB+1);
            B.setOnAction(e -> {selectedStar = B;});
            stars.add(B);
            layout.getChildren().add(B);
            B.getStylesheets().add("RateByStars.css");
        }
    }

    public void addRunnable(Runnable A) {
        for (Button S : stars) {
            S.setOnAction(e -> {selectedStar=S; A.run();});
        }
    }

    public int getRating() {
       if (selectedStar==null)
           return 0;
       else
        return (int) selectedStar.getUserData();
   };

    public Node asNode() {return layout;}

}
