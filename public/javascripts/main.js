var backend = {
    loadCommentsFromServer: function(settings) {
        // ugly mutable transformation
        settings.url = '/comments.json';
        settings.dataType = 'json';

        $.ajax(settings);
    },
    handleCommentSubmit: function(settings) {
        // ugly mutable transformation
        settings.url = '/comments.json';
        settings.dataType = 'json';
        settings.contentType = 'application/json';
        settings.type = 'POST';

        $.ajax(settings);
    }
};

if (document.getElementById('commentBox') == null) {
    React.renderComponent(
        CommentBox(backend)({data: []}),
        document.getElementById('content')
    );
} else {
    // commentBox already there
    // bind the client side component only with data
    backend.loadCommentsFromServer({
        success: function(data) {
            React.renderComponent(
                CommentBox(backend)({data: data}),
                document.getElementById('content')
            );
        },
        error: function(xhr, status, err) {
            console.error(status, err.toString());
        }
    });
}
