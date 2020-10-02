# Popular Movies

Popular Movies is an android app to show a set of movies organized in cards. This app fetches data from The Movie Database API Features include search by title, order by rating and trending.

This is an on-going project, I have more features planned.


## Usage

In order to use this app, you need to request a personal api-key. It's a simple process, check [The Movie Database API](https://www.themoviedb.org/documentation/api).

Next you need to paste your key in TWO source classes.
First is MainActivity.java and second is DetailActvity.java

Let's use MainActivity as example:

```java
//directory --> app/src/main/java/com/example/popularmovies/MainActivity.java 

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

private RecyclerView recyclerView;
private MoviesAdapter adapter;
private List<Movie> movieList;
private static final String API_TOKEN = "your_api_key"; //HERE
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)