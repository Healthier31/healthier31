import { styled } from "styled-components";

import Title from "./Title";
import EditBox from "./EditBox";

const DirectoryEditBox = ({
  EditPage,
  category,
  changeCategory,
  postCategory,
}) => {
  return (
    <>
      <EditSection>
        <Title EditPage={EditPage} />
        <EditBox
          EditPage={EditPage}
          category={category}
          changeCategory={changeCategory}
          postCategory={postCategory}
        />
      </EditSection>
    </>
  );
};

export default DirectoryEditBox;

const EditSection = styled.section`
  width: 100%;

  padding: 0px 35px;
`;
