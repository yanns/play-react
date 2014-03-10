var React = require('./react');

var CommentForm = React.createClass({
    handleSubmit: function() {
        var author = this.refs.author.getDOMNode().value.trim();
        var text = this.refs.text.getDOMNode().value.trim();
        if (!text || !author) {
            return false;
        }
        this.props.onCommentSubmit({author: author, text: text});
        this.refs.author.getDOMNode().value = '';
        this.refs.text.getDOMNode().value = '';
        return false;
    },
    render: function() {
        return (
            React.DOM.form({
                className: 'commentForm',
                onSubmit: this.handleSubmit,
                children: [
                    React.DOM.input({type: 'text', ref:'author', placeholder: 'Your name'}),
                    React.DOM.input({type: 'text', ref:'text', placeholder: 'Say something...'}),
                    React.DOM.input({type: 'submit', value: 'Post'})
                ]
            })
        );
    }
});

module.exports = CommentForm;
