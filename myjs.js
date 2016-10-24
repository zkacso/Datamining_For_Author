$("document").ready(loadArticles);
        
function loadArticles()
{
    var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    url += '?' + $.param({
      'begin_date': "20150101",
      'end_date': "20161010",
      'fl': "web_url,byline",
      'page': 0
    });
    $.ajax({
      url: url,
      method: 'GET',
    }).done(function(result) {
      $("#articles").html(result);
    }).fail(function(err) {
      throw err;
    });
}