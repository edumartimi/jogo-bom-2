package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.java.swing.ui.SplashScreen;



import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {


	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	private Texture moeda;

	private Texture moeda2;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;

	private Circle batarang;

	private Circle batarang2;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima=0;
	private boolean passouCano=false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;

	private float posicaoMoedaHorizontal = 0;

	private float posicaoMoedaHorizontal2 = 0;

	private float posicaoMoedaVertical =0;

	private float posicaoMoedaVertical2 =0;




	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	Preferences preferencias;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;


	public SpriteBatch batch;
	public Texture img;


	//Metodo que chama outros metodos quando o aplicativo é aberto
	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();

		// Cria um objeto SpriteBatch para desenhar a tela de splash
		batch = new SpriteBatch();


	}


	//Metodo que chama metodos de relacionamento de imagens quando o aplicativo é aberto 
	@Override
	public void render () {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	//metodo que atribui as imagens as suas respectivas variaveis
	
	public void inicializarTexturas()
	{
		passaros = new Texture[3];
		passaros[0] = new Texture("bat1.png");
		passaros[1] = new Texture("bat2.png");
		passaros[2] = new Texture("bat3.png");

		fundo = new Texture("fundo.jpg");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		moeda = new Texture("batarang.png");
		moeda2 = new Texture("batarang2.png");

	}
	
	//metodo que passa atributo para as variaveis que não são imagens
	public void inicializarObjetos()
	{
		batch = new SpriteBatch();
		random	= new Random();

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo/2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoMoedaHorizontal = larguraDispositivo;
		posicaoMoedaHorizontal2 = larguraDispositivo;
		espacoEntreCanos = 350;



		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao= new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);


		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		batarang = new Circle();
		batarang2 = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();

		somVoando =  Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao =  Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao =  Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima",0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2 ,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
	}

	//metodo que verifica qual é o estado do jogo, e se o estado for igual a 1 ele faz com que o jogo funcione, se for igual a 2 ele faz com que o jogo pare e se a pontuação atual for maior que o record anterior ela é substituida
	private void verificarEstadoJogo()
	{
		boolean toqueTela = Gdx.input.justTouched();
		if(estadoJogo==0)
		{
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
		}
		else if (estadoJogo ==1)
		{
			if (toqueTela) {
				gravidade = -15;

				somVoando.play();

				if(pontos>50)
				{
					gravidade=-10;
				}
			}
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if(pontos>50)
			{
				posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 400;
				posicaoMoedaHorizontal -= Gdx.graphics.getDeltaTime()*400;
				posicaoMoedaHorizontal2 -= Gdx.graphics.getDeltaTime()*399;
			}
			if(pontos>1000)
			{
				posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 600;
				posicaoMoedaHorizontal -= Gdx.graphics.getDeltaTime()*600;
				posicaoMoedaHorizontal2 -= Gdx.graphics.getDeltaTime()*599;
			}
			posicaoMoedaHorizontal -= Gdx.graphics.getDeltaTime()*200;
			posicaoMoedaHorizontal2 -= Gdx.graphics.getDeltaTime()*199;
			if (posicaoCanoHorizontal< -canoTopo.getWidth())
			{
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoMoedaHorizontal = random.nextInt(720)+50;
				posicaoMoedaHorizontal2 = random.nextInt(719)+51;
				posicaoCanoVertical = random.nextInt(400) - 200 ;
				posicaoMoedaVertical = random.nextInt(600)-300;
				posicaoMoedaVertical2 = random.nextInt(599)-301;
				passouCano = false;
			}
			if (posicaoInicialVerticalPassaro>0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;
		}
		else if (estadoJogo ==2)
		{
			if (pontos>pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima",pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*-1000;
			if (toqueTela)
			{
				estadoJogo = 0;
				pontos =0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
			}
		}
	}
	
	//metodo que detecta se um objeto esta dentro do outro, caso esteja ele detecta a colisão muda o estado do jogo para 2
	private void detectarColisoes()
	{
		circuloPassaro.set(50+posicaoHorizontalPassaro+passaros[0].getWidth()/2,posicaoInicialVerticalPassaro+passaros[0].getWidth()/2,passaros[0].getWidth()/2);

		batarang.set(posicaoMoedaHorizontal,alturaDispositivo/2 + espacoEntreCanos / 2+posicaoCanoVertical,moeda.getWidth()/2);
		batarang2.set(posicaoMoedaHorizontal2,alturaDispositivo/2 + espacoEntreCanos / 2+posicaoCanoVertical,moeda.getWidth()/2);


		retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

		retanguloCanoCima.set(posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+posicaoCanoVertical,canoTopo.getWidth(),canoTopo.getHeight());

		boolean colidiucanocima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiucanobaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

		boolean colidiumoeda = Intersector.overlaps(batarang,circuloPassaro);
		boolean colidiumoeda2 = Intersector.overlaps(batarang2,circuloPassaro);




		if(colidiucanocima || colidiucanobaixo)
		{
			if(estadoJogo == 1)
			{
				somColisao.play();
				estadoJogo = 2;

			}
		}
		if(colidiumoeda)
		{
			somColisao.play();
			pontos= pontos+10;
			posicaoMoedaHorizontal -= Gdx.graphics.getDeltaTime()*559900;

		}

		if(colidiumoeda2)
		{
			somColisao.play();
			pontos= pontos+5;
			posicaoMoedaHorizontal2 -= Gdx.graphics.getDeltaTime()*559900;

		}
	}
	
	//metodo que desenha o que foi guardado nas variaveis dos metodos passados

	private  void desenharTexturas()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
		batch.draw(moeda2,posicaoMoedaHorizontal2,alturaDispositivo/2 + espacoEntreCanos / 2+posicaoCanoVertical);
		batch.draw(moeda,posicaoMoedaHorizontal,alturaDispositivo/2 + espacoEntreCanos / 2+posicaoCanoVertical);
		batch.draw(passaros[(int)variacao],50+posicaoHorizontalPassaro,posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo,posicaoCanoHorizontal,alturaDispositivo/2 - canoBaixo.getHeight() - 	 espacoEntreCanos/2+posicaoCanoVertical);
		batch.draw(canoTopo,posicaoCanoHorizontal,alturaDispositivo/2 + espacoEntreCanos / 2+posicaoCanoVertical);
		textoPontuacao.draw(batch, String.valueOf(pontos),larguraDispositivo/2,alturaDispositivo-110);

		if(estadoJogo == 2)
		{
			batch.draw(gameOver,larguraDispositivo/2-gameOver.getWidth()/2,alturaDispositivo/2);
			textoReiniciar.draw(batch,"toque para reiniciar",larguraDispositivo/2-140,alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é: "+pontuacaoMaxima+" pontos", larguraDispositivo/2-140,alturaDispositivo/2 - gameOver.getHeight());
		}
		batch.end();
	}
	
	//se o player estiver na frente do cano ele adiciona 1 ponto

	private void validarPontos()
	{
			if(posicaoCanoHorizontal<50-passaros[0].getWidth())
			{
				if(!passouCano)
				{
					pontos++;
					passouCano = true;
					somPontuacao.play();
				}
			}

			variacao+= Gdx.graphics.getDeltaTime()*10;
			if(variacao>3)
			{
				variacao = 0;
			}
	}

	//metodo que redimenciona o jogo para fica inteiro na tela do celular
	@Override
	public void resize(int width, int height)
	{
		viewport.update(width,height);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}
