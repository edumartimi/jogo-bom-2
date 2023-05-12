import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen extends ScreenAdapter {
    private Texture splashTexture;
    private SpriteBatch batch;

    public SplashScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        // Carrega a imagem da tela de splash
        splashTexture = new Texture("badlogic.jpg");
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com uma cor de fundo
        batch.begin();
        batch.draw(splashTexture, 0, 0);
        batch.end();

        // Verifica se a imagem da tela de splash foi carregada e carrega a próxima tela se necessário
        if (splashTexture != null && splashTexture.getTextureObjectHandle() != 0) {
            // Aqui você pode carregar a próxima tela do seu jogo
        }
    }

    @Override
    public void dispose() {
        // Descarta os recursos da tela de splash
        splashTexture.dispose();
    }
}