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
