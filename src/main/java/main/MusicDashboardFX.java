package main;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.LogicEngine;
import model.Lagu;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class MusicDashboardFX extends Application {

    // === MESIN DATA PBO LU ===
    private List<Lagu> rawList = new ArrayList<>();
    private LogicEngine<Lagu> engine = new LogicEngine<>();
    
    // === KOMPONEN UI UTAMA ===
    private FlowPane mainGrid = new FlowPane();
    private Label lblNowPlayingTitle = new Label("Pilih lagu untuk memutar");
    private Label lblNowPlayingArtist = new Label("Symphony Elite");
    private Random random = new Random();

    // Resource Gambar
    private final String PLACEHOLDER_IMG = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?q=80&w=1000&auto=format&fit=crop";
    private final String BACKDROP_IMG = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=1280&auto=format&fit=crop";

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050505;");

        // 1. LAYER BACKGROUND
        ImageView backdrop = new ImageView(new Image(BACKDROP_IMG));
        backdrop.setFitWidth(1400);
        backdrop.setPreserveRatio(true);
        backdrop.setOpacity(0.3);
        applyKenBurnsEffect(backdrop);
        
        Pane particleLayer = new Pane();
        createParticleEngine(particleLayer);

        // 2. LAYER UI (INTERAKTIF)
        HBox mainLayout = new HBox();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setSpacing(20);

        // --- SIDEBAR (Form Tambah Lagu & Menu) ---
        VBox sidebar = createInteractiveSidebar();
        
        // --- CONTENT AREA ---
        VBox contentArea = new VBox(25);
        contentArea.setPadding(new Insets(20));
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Header Text
        Label headerTitle = new Label("DISCOVER ELITE");
        headerTitle.setTextFill(Color.WHITE);
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 42));
        headerTitle.setEffect(new DropShadow(20, Color.GOLD));

        // Control Panel (Search & Sort)
        HBox topControls = new HBox(15);
        topControls.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Cari ID atau Judul (Enter)...");
        txtSearch.getStyleClass().add("luxury-search");
        txtSearch.setPrefWidth(300);

        Button btnSortId = createOutlineButton("Sort by ID");
        Button btnSortNama = createOutlineButton("Sort by Title");
        Button btnRefresh = createOutlineButton("Refresh");

        topControls.getChildren().addAll(txtSearch, btnSortId, btnSortNama, btnRefresh);
        
        // Grid Content (Tempat Kartu Lagu)
        ScrollPane scrollPane = new ScrollPane(mainGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("luxury-scroll");
        mainGrid.setHgap(25);
        mainGrid.setVgap(25);
        mainGrid.setPadding(new Insets(10));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        contentArea.getChildren().addAll(headerTitle, topControls, scrollPane);
        mainLayout.getChildren().addAll(sidebar, contentArea);

        // --- BOTTOM PLAYER BAR ---
        VBox playerBar = createPlayerBar();
        
        BorderPane finalLayout = new BorderPane();
        finalLayout.setCenter(mainLayout);
        finalLayout.setBottom(playerBar);

        root.getChildren().addAll(backdrop, particleLayer, finalLayout);

        // ================= LOGIKA EVENT HANDLER (PBO ACTIVE!) =================
        
        // Fitur Search (Manggil smartSearch dari LogicEngine)
        txtSearch.setOnAction(e -> {
            String key = txtSearch.getText();
            if (key.isEmpty()) { refreshGrid(); return; }
            Lagu hasil = engine.smartSearch(rawList, key);
            mainGrid.getChildren().clear(); // Bersihin layar
            if (hasil != null) {
                addSongCard(hasil); // Tampilkan hasil cari
            } else {
                showAlert("Not Found", "Lagu tidak ditemukan di sistem bre!");
            }
        });

        // Fitur Sortir
        btnSortId.setOnAction(e -> { engine.sortById(rawList); refreshGrid(); });
        btnSortNama.setOnAction(e -> { engine.sortByNama(rawList); refreshGrid(); });
        btnRefresh.setOnAction(e -> { txtSearch.clear(); refreshGrid(); });

        // Setup Scene
        Scene scene = new Scene(root, 1280, 800);
        injectModernCSS(scene);
        
        primaryStage.setTitle("Symphony Elite - Fully Interactive PBO");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load Data Awal
        initDummyData();
    }

    // ================= FUNGSI-FUNGSI PEMBUAT UI =================

    private VBox createInteractiveSidebar() {
        VBox sb = new VBox(25);
        sb.setPrefWidth(280);
        sb.setPadding(new Insets(30, 20, 20, 20));
        sb.getStyleClass().add("sidebar-glass");

        Label logo = new Label("SYMPHONY");
        logo.setTextFill(Color.GOLD);
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        // Form Tambah Data PBO (Biar bisa nambah lagu betulan)
        VBox form = new VBox(15);
        Label formTitle = new Label("➕ Add New Track");
        formTitle.setTextFill(Color.WHITE);
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextField txtId = new TextField(); txtId.setPromptText("ID Lagu"); txtId.getStyleClass().add("luxury-input");
        TextField txtJudul = new TextField(); txtJudul.setPromptText("Judul Lagu"); txtJudul.getStyleClass().add("luxury-input");
        TextField txtArtis = new TextField(); txtArtis.setPromptText("Nama Artis"); txtArtis.getStyleClass().add("luxury-input");
        
        Button btnAdd = new Button("Inject to System");
        btnAdd.getStyleClass().add("luxury-btn");
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        // Event Tambah Lagu
        btnAdd.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                rawList.add(new Lagu(id, txtJudul.getText(), txtArtis.getText()));
                refreshGrid();
                txtId.clear(); txtJudul.clear(); txtArtis.clear();
            } catch (Exception ex) {
                showAlert("Error", "Isi ID pake angka bro!");
            }
        });

        form.getChildren().addAll(formTitle, txtId, txtJudul, txtArtis, btnAdd);
        
        sb.getChildren().addAll(logo, new Separator(), form);
        return sb;
    }

    private VBox createPlayerBar() {
        VBox bar = new VBox(10);
        bar.setPadding(new Insets(15, 40, 15, 40));
        bar.getStyleClass().add("player-bar");

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);

        // Info Lagu Kiri
        VBox songInfo = new VBox(3);
        lblNowPlayingTitle.setTextFill(Color.WHITE);
        lblNowPlayingTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNowPlayingArtist.setTextFill(Color.GOLD);
        lblNowPlayingArtist.setFont(Font.font("Segoe UI", 12));
        songInfo.getChildren().addAll(lblNowPlayingTitle, lblNowPlayingArtist);
        songInfo.setPrefWidth(300);

        // Tombol Tengah
        HBox controls = new HBox(30);
        controls.setAlignment(Pos.CENTER);
        Label btnPrev = new Label("⏮"); btnPrev.setStyle("-fx-text-fill: gray; -fx-font-size: 20px;");
        Label btnPlay = new Label("▶"); btnPlay.setStyle("-fx-font-size: 30px; -fx-text-fill: white; -fx-cursor: hand;");
        Label btnNext = new Label("⏭"); btnNext.setStyle("-fx-text-fill: gray; -fx-font-size: 20px;");
        controls.getChildren().addAll(btnPrev, btnPlay, btnNext);
        
        HBox.setHgrow(controls, Priority.ALWAYS);

        layout.getChildren().addAll(songInfo, controls, new Region()); // Region buat balance spacing
        
        // Progress bar panjang di bawah
        ProgressBar progress = new ProgressBar(0.0);
        progress.setPrefWidth(1200);
        progress.setMaxWidth(Double.MAX_VALUE);
        
        // Animasi palsu biar bar-nya jalan kalau di play
        btnPlay.setOnMouseClicked(e -> {
            if(btnPlay.getText().equals("▶")) {
                btnPlay.setText("⏸");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), new KeyValue(progress.progressProperty(), 1.0)));
                timeline.play();
            } else {
                btnPlay.setText("▶");
            }
        });

        bar.getChildren().addAll(layout, progress);
        return bar;
    }

    private void addSongCard(Lagu lagu) {
        VBox card = new VBox(12);
        card.getStyleClass().add("song-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);

        ImageView cover = new ImageView(new Image(PLACEHOLDER_IMG));
        cover.setFitWidth(170); cover.setFitHeight(170);
        Rectangle clip = new Rectangle(170, 170);
        clip.setArcWidth(20); clip.setArcHeight(20);
        cover.setClip(clip);

        Label title = new Label(lagu.getNama());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label artist = new Label("ID: " + lagu.getId() + " | " + lagu.getDetailTambahan());
        artist.setTextFill(Color.GRAY);
        artist.setFont(Font.font("Segoe UI", 12));

        card.getChildren().addAll(cover, title, artist);
        
        // EVENT: KLIK KARTU BUAT MUTER LAGU
        card.setOnMouseClicked(e -> {
            lblNowPlayingTitle.setText(lagu.getNama());
            lblNowPlayingArtist.setText(lagu.getDetailTambahan());
            card.setEffect(new DropShadow(30, Color.CYAN)); // Glow pas diklik
        });

        // Hover Effect
        card.setOnMouseEntered(e -> card.setScaleX(1.05));
        card.setOnMouseExited(e -> { card.setScaleX(1.0); card.setEffect(null); });

        mainGrid.getChildren().add(card);
    }

    // Refresh UI sesuai isi List rawList (setelah di sort / add)
    private void refreshGrid() {
        mainGrid.getChildren().clear();
        for(Lagu l : rawList) { addSongCard(l); }
    }

    private Button createOutlineButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("outline-btn");
        return btn;
    }

    private void initDummyData() {
        rawList.add(new Lagu(101, "Cyberpunk Sonata", "Beethoven 2077"));
        rawList.add(new Lagu(55, "Neon Lights", "The Synth"));
        rawList.add(new Lagu(12, "Galactic Empire", "Hans Zimmer"));
        rawList.add(new Lagu(88, "Digital Rhapsody", "Queen.exe"));
        refreshGrid();
    }

    private void applyKenBurnsEffect(ImageView iv) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(25), iv);
        st.setFromX(1.0); st.setFromY(1.0); st.setToX(1.15); st.setToY(1.15);
        st.setCycleCount(Animation.INDEFINITE); st.setAutoReverse(true); st.play();
    }

    private void createParticleEngine(Pane layer) {
        for (int i = 0; i < 40; i++) {
            Circle p = new Circle(random.nextDouble() * 2, Color.web("gold", 0.4));
            p.setTranslateX(random.nextDouble() * 1280); p.setTranslateY(random.nextDouble() * 800);
            layer.getChildren().add(p);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(10 + random.nextInt(10)), p);
            tt.setByY(-200); tt.setCycleCount(Animation.INDEFINITE); tt.play();
        }
    }

    private void injectModernCSS(Scene scene) {
        String css = "" +
            ".sidebar-glass { -fx-background-color: rgba(10,10,15,0.8); -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 1 0 0; }" +
            ".luxury-search { -fx-background-color: rgba(30,30,40,0.8); -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 20; -fx-border-color: rgba(255,215,0,0.3); -fx-border-radius: 20; }" +
            ".luxury-input { -fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 10; }" +
            ".luxury-btn { -fx-background-color: gold; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand; }" +
            ".luxury-btn:hover { -fx-background-color: white; }" +
            ".outline-btn { -fx-background-color: transparent; -fx-text-fill: gold; -fx-border-color: gold; -fx-border-radius: 15; -fx-padding: 8 15; -fx-cursor: hand; }" +
            ".outline-btn:hover { -fx-background-color: rgba(255,215,0,0.2); }" +
            ".luxury-scroll { -fx-background: transparent; -fx-background-color: transparent; }" +
            ".luxury-scroll .viewport { -fx-background-color: transparent; }" +
            ".song-card { -fx-background-color: rgba(20,20,25,0.8); -fx-background-radius: 15; -fx-cursor: hand; -fx-transition: 0.3s; }" +
            ".player-bar { -fx-background-color: rgba(5,5,8,0.95); -fx-border-color: rgba(255,215,0,0.2); -fx-border-width: 1 0 0 0; }" +
            ".progress-bar .track { -fx-background-color: #222; -fx-background-radius: 5; }" +
            ".progress-bar .bar { -fx-background-color: linear-gradient(to right, gold, cyan); -fx-background-radius: 5; }";
        
        scene.getStylesheets().add("data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes()));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}