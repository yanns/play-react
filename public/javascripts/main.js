var CommentBox = React.createClass({
    render: function() {
        return (
            React.DOM.div({
                className: 'commentBox',
                children: 'Hello, world! I am a CommentBox.'
            })
        );
    }
});
React.renderComponent(
    CommentBox({}),
    document.getElementById('content')
);