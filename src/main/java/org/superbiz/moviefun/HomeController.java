package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private PlatformTransactionManager getAlbumFactoryBean;
    private PlatformTransactionManager getMovieFactoryBean;
    private final TransactionTemplate albumstransactionTemplate;
    private final TransactionTemplate moviestransactionTemplate;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          PlatformTransactionManager getAlbumsFactoryBean,
                          PlatformTransactionManager getMoviesFactoryBean) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.getAlbumFactoryBean = getAlbumsFactoryBean;
        this.getMovieFactoryBean = getMoviesFactoryBean;
        this.albumstransactionTemplate = new TransactionTemplate(getAlbumFactoryBean);
        this.moviestransactionTemplate = new TransactionTemplate(getMovieFactoryBean);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        for (Movie movie : movieFixtures.load()) {
            moviestransactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    moviesBean.addMovie(movie);
                }
            });
        }



        for (Album album : albumFixtures.load()) {

            albumstransactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    albumsBean.addAlbum(album);
                }
            });


        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
