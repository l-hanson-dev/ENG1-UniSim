package eng1.unisim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import eng1.unisim.managers.UIManager.BuildingSelectionCallback;
import eng1.unisim.models.Building;

import java.util.HashMap;
import java.util.Map;

/**
 * displays a vertical list of building types that can be placed in the game world
 * each building type has an icon button and a counter showing how many have been placed
 */
public class BuildingInventoryView extends Table implements Disposable {
    // available building types that can be placed in the game
    private static final String[] BUILDING_TYPES = { "Accommodation", "Learning", "Dining", "Recreation" };
    // maps to track the ui labels and counts for each building type
    private final Map<String, Label> buildingCounters = new HashMap<>();
    private final Map<String, Integer> buildingCounts = new HashMap<>();
    // stores the button textures for each building type
    private final Map<String, Texture> buildingTextures = new HashMap<>();

    /**
     * creates the building inventory ui panel
     * @param font the font to use for counter labels
     * @param buildingCallback callback triggered when a building is selected
     */
    public BuildingInventoryView(BitmapFont font, BuildingSelectionCallback buildingCallback) {
        this.top().left();
        this.setFillParent(true);
        this.pad(10);

        initializeBuildingCounts();
        loadTextures();
        createBuildingButtons(font, buildingCallback);
    }

    // initialize the counter for each building type to zero
    private void initializeBuildingCounts() {
        for (String type : BUILDING_TYPES) {
            buildingCounts.put(type, 0);
        }
    }

    // load the icon texture for each building type from the assets folder
    private void loadTextures() {
        for (String type : BUILDING_TYPES) {
            buildingTextures.put(type, new Texture(Gdx.files.internal("buildings/" + type.toLowerCase() + ".png")));
        }
    }

    /**
     * creates the ui elements for each building type:
     * - an image button with the building's icon
     * - a label showing how many of that building exist
     */
    private void createBuildingButtons(BitmapFont font, BuildingSelectionCallback buildingCallback) {
        Label.LabelStyle counterStyle = new Label.LabelStyle(font, Color.WHITE);

        for (String type : BUILDING_TYPES) {
            ImageButton.ImageButtonStyle style = createButtonStyle(type);
            ImageButton button = createBuildingButton(type, style, buildingCallback);
            Label counter = new Label("0", counterStyle);
            buildingCounters.put(type, counter);

            this.add(button).size(100, 100).pad(5);
            this.add(counter).pad(5).row();
        }
    }

    // creates the visual style for a building's button using its texture
    private ImageButton.ImageButtonStyle createButtonStyle(String type) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        Texture texture = buildingTextures.get(type);
        style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageChecked = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageChecked.setMinWidth(90);
        style.imageChecked.setMinHeight(90);
        return style;
    }

    /**
     * creates a button that spawns a new building when clicked
     * the building's stats are currently hardcoded
     */
    private ImageButton createBuildingButton(String type, ImageButton.ImageButtonStyle style,
                                             BuildingSelectionCallback buildingCallback) {
        ImageButton button = new ImageButton(style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Building building = new Building(type, 50000, 10, 5000);
                buildingCallback.onBuildingSelected(building);
                button.setChecked(true);
            }
        });
        return button;
    }

    /**
     * updates the displayed count for a building type
     * called when buildings are added or removed from the game
     */
    public void updateBuildingCount(String buildingType, int count) {
        buildingCounts.put(buildingType, count);
        Label counter = buildingCounters.get(buildingType);
        if (counter != null) {
            counter.setText(String.valueOf(count));
        }
    }

    // clean up textures when this ui element is no longer needed
    @Override
    public void dispose() {
        for (Texture texture : buildingTextures.values()) {
            texture.dispose();
        }
    }
}
